package com.wex.currencyconverter.DTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TransactionCreatedDTOTest {
    private TransactionCreatedDTO transactionCreatedDTO;

    @Autowired
    private JacksonTester<TransactionCreatedDTO> jacksonTester;

    private ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    @EnableCaching
    static class TransactionCreatedDTOTestConfig {
        @Bean
        public CacheManager cacheManager() {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
            cacheManager.setCaffeine(Caffeine.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(10, TimeUnit.MINUTES));
            return cacheManager;
        }
    }

    @BeforeEach
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.OCTOBER, 15, 0, 0, 0);
        Date date = calendar.getTime();
        transactionCreatedDTO = transactionCreatedDTO.builder()
                .date(date)
                .transactionId("TXN-1234567890")
                .description("New Watch")
                .amount(BigDecimal.valueOf(150.69))
                .build();
    }

    @Test
    public void test_Serialize() throws Exception {
        //Assert
        assertThat(jacksonTester.write(transactionCreatedDTO)).isNotNull();
        assertThat(jacksonTester.write(transactionCreatedDTO))
                .hasJsonPathStringValue("@.transactionId")
                .extractingJsonPathStringValue("@.transactionId")
                .isEqualTo("TXN-1234567890");
        assertThat(jacksonTester.write(transactionCreatedDTO))
                .hasJsonPathStringValue("@.amount")
                .extractingJsonPathStringValue("@.amount")
                .isEqualTo("150.69");

    }

    @Test
    void test_Deserialization() throws Exception {

        // Arrange
        String content = "{\"date\":\"2023-10-15 00:00:00\",\"transactionId\":\"TXN-1234567890\",\"description\":\"New Watch\",\"amount\":\"150.69\"}";

        // Act
        TransactionDTO transactionDTO = objectMapper.readValue(content, TransactionDTO.class);

        //Assert
        assertThat(transactionCreatedDTO.getTransactionId()).isEqualTo(this.transactionCreatedDTO.getTransactionId());
        assertThat(transactionCreatedDTO.getDescription()).isEqualTo(this.transactionCreatedDTO.getDescription());
        assertThat(transactionCreatedDTO.getAmount()).isEqualTo(this.transactionCreatedDTO.getAmount());
    }
}
