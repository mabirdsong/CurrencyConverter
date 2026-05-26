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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CurrencyExchangeRateDTOTest {

    private CurrencyExchangeRateDTO  currencyExchangeRateDTO;
    private final String Currency_Code = "Canada-Dollar";

    @Autowired
    private JacksonTester<CurrencyExchangeRateDTO> json;

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
        currencyExchangeRateDTO = CurrencyExchangeRateDTO.builder()
                .Record_Date(date)
                .Exchange_Rate(BigDecimal.valueOf(1.359))
                .Country_Currency_Desc(Currency_Code)
                .build();
    }

    @Test
    void testSerialization() throws Exception {
        // Assert that the 'country_currency_desc' field maps to 'Country_Currency_Desc' in JSON
        assertThat(this.json.write(currencyExchangeRateDTO))
                .hasJsonPathStringValue("@.country_currency_desc")
                .extractingJsonPathStringValue("@.country_currency_desc")
                .isEqualTo(Currency_Code);
    }

    @Test
    void testDeserialization() throws Exception {
        String content = "{\"record_date\":\"2023-10-15T00:00:00.000\",\"country_currency_desc\":\"Canada-Dollar\",\"exchange_rate\":\"1.359\"}";

        // Assert that 'country_currency_desc' in JSON maps to the 'country_currency_desc' field in Java
        assertThat(this.json.parse(content)).isNotNull();
        assertThat(this.json.parseObject(content).getCountry_Currency_Desc()).isEqualTo(Currency_Code);
    }
}