package com.wex.currencyconverter.DTO;

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

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ApiResponseTest {
    private ApiResponse apiResponse;
    private final String MESSAGE = "Successful";
    private final String DATA = "data";

    @Autowired
    private JacksonTester<ApiResponse> jacksonTester;

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
        apiResponse = ApiResponse.builder()
                .message(MESSAGE)
                .data(DATA)
                .build();
    }

    @Test
    public void testConstructor() {
        //Assert
        assertThat(apiResponse.getMessage()).isEqualTo(MESSAGE);
        assertThat(apiResponse.getData()).isEqualTo(DATA);
    }

    @Test
    void testSerialization() throws Exception {
        // Assert that the 'message' field maps to 'message' in JSON
        assertThat(this.jacksonTester.write(apiResponse))
                .hasJsonPathStringValue("@.message")
                .extractingJsonPathStringValue("@.message")
                .isEqualTo(MESSAGE);

        assertThat(this.jacksonTester.write(apiResponse))
                .hasJsonPathStringValue("@.data")
                .extractingJsonPathStringValue("@.data")
                .isEqualTo(DATA);
    }
}
