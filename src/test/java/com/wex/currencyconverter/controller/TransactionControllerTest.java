package com.wex.currencyconverter.controller;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.wex.currencyconverter.DTO.TransactionCreatedDTO;
import com.wex.currencyconverter.DTO.TransactionDTO;
import com.wex.currencyconverter.DTO.TransactionRequestDTO;
import com.wex.currencyconverter.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(TransactionController.class)
@EnableCaching
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    @EnableCaching
    static class TransactionControllerTestConfig {

        @Bean
        public CacheManager cacheManager() {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
            cacheManager.setCaffeine(Caffeine.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(10, TimeUnit.MINUTES));
            return cacheManager;
        }
    }

    @Test
    void test_GetTransactions() throws Exception {
        // Arrange
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .date(new Date())
                .description("description")
                .transactionId("TXN-4561234567")
                .amount(BigDecimal.valueOf(123.56))
                .countryCurrency("Canada-Dollar")
                .exchangeRate(BigDecimal.valueOf(1.2))
                .amount(BigDecimal.valueOf(148.27))
                .build();
        List<TransactionDTO> transactionDTOList = Arrays.asList(transactionDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page page = new PageImpl<> (transactionDTOList, pageable, 1);
        when(transactionService.getTransactions(anyString(), anyInt(), anyInt(), anyString(), anyBoolean())).thenReturn(page);

        //Act & Assert
        mockMvc.perform(get("/api/v1/transaction/listconversionrates/countrycurrency/Canada-Dollar")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.[*].amount").value("148.27"))
                .andExpect(jsonPath("$.data.content.[*].exchangeRate").value(1.2))
                .andExpect(jsonPath("$.data.content.[*].description").value("description"));
    }

    @Test
    void test_Create() throws Exception {
        // Arrange
        TransactionRequestDTO transactionRequestDTO = TransactionRequestDTO.builder()
                .amount(BigDecimal.valueOf(223.56))
                .date(new Date(LocalDate.now().toEpochDay()))
                .description("New Shoes Reto Jordan 1s")
                .build();
        TransactionCreatedDTO transactionCreatedDTO = TransactionCreatedDTO.builder()
                .date(new Date())
                .description("New Shoes Reto Jordan 1s")
                .transactionId("TXN-4561234567")
                .amount(BigDecimal.valueOf(223.56))
                .build();

        when(transactionService.create(transactionRequestDTO)).thenReturn(transactionCreatedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transaction/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.data.amount").value(223.56))
                        .andExpect(jsonPath("$.data.description").value("New Shoes Reto Jordan 1s"));

    }
}
