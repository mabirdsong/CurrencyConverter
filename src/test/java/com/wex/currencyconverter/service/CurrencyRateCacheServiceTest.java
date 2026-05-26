package com.wex.currencyconverter.service;

import com.wex.currencyconverter.utility.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CurrencyRateCacheServiceTest {

    private String countryCurrency = "USD";


    @Autowired
    private CurrencyRateCacheService currencyRateCacheService;

    @Test
    void test_GenerateCacheKeyReturnSeptember() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.DECEMBER, 23, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFSEPTEMBER);
    }

    @Test
    void test_GenerateCacheKeyReturnDecember() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.FEBRUARY, 23, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);
        localDateTime = localDateTime.minusYears(1);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFDECEMBER);
    }

    @Test
    void test_GenerateCacheKeyReturnJune() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.SEPTEMBER, 23, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFJUNE);
    }

    @Test
    void test_GenerateCacheKeyReturnMarch() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.JUNE, 23, 0, 0, 0 );
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFMARCH);
    }

    @Test
    void test_GenerateCacheKeyReturnExactMatchMarch() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.MARCH, 31, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFMARCH);
    }

    @Test
    void test_GenerateCacheKeyReturnExactMatchJune() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.JUNE, 30, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFJUNE);
    }

    @Test
    void test_GenerateCacheKeyReturnExactMatchSeptember() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.SEPTEMBER, 30, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFSEPTEMBER);
    }

    @Test
    void test_GenerateCacheKeyReturnExactMatchDecember() {
        // Arrange
        String countryCurrency = "USD";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.DECEMBER, 31, 0, 0, 0);
        Date transactionDate = calendar.getTime();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);

        //Act
        String result = currencyRateCacheService.generateCacheKey(countryCurrency, transactionDate);

        //Assert
        assertThat(result).isEqualTo(countryCurrency + localDateTime.getYear() + Constants.LASTDAYOFDECEMBER);
    }
}
