package com.wex.currencyconverter.DTO;

import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TransactionRequestDTOTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private TransactionRequestDTO transactionRequestDTO;

    @Autowired
    private JacksonTester<TransactionRequestDTO> tester;

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

    @BeforeEach
    public void setup() {
        transactionRequestDTO = TransactionRequestDTO.builder()
                .date(new Date())
                .description("Test")
                .amount(BigDecimal.valueOf(123.45))
                .build();
    }

    @Test
    public void test_EmptyConstructor() {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        var violations = validator.validate(transactionRequestDTO);
        assertThat(violations).hasSize(4);
    }

    @Test
    public void test_DescriptionLenth() {
        transactionRequestDTO.setDescription("Test Test Test Test Test Test Test Test Test Test Test");
        var violations = validator.validate(transactionRequestDTO);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void test_BuilderComplete() {
        var violations = validator.validate(transactionRequestDTO);
        assertThat(violations).hasSize(0);
    }

    @Test
    public void test_Serialization() throws IOException {
        assertThat(tester.write(transactionRequestDTO)).isNotNull();
        assertThat(tester.write(transactionRequestDTO)).hasJsonPathStringValue("@.description", transactionRequestDTO.getDescription());
    }
}
