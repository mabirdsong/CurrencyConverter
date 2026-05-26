package com.wex.currencyconverter.service;

import com.wex.currencyconverter.DTO.*;
import com.wex.currencyconverter.model.Transaction;
import com.wex.currencyconverter.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CurrencyRateCacheService currencyRateCacheService;

    @Mock
    private SecureRandom secureRandom;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void test_Create() {
        //Arrange
        TransactionRequestDTO transactionRequestDTO = TransactionRequestDTO.builder()
                .date(new Date())
                .description("New Shoes Jordans")
                .amount(BigDecimal.valueOf(265.32))
                .build();
        Transaction transaction = Transaction.builder()
                        .date(transactionRequestDTO.getDate())
                        .description(transactionRequestDTO.getDescription())
                        .amount(transactionRequestDTO.getAmount())
                        .transactionId("TXN-1234567890")
                        .build();

        // Act
        TransactionCreatedDTO transactionCreatedDTO = transactionService.create(transactionRequestDTO);

        // Assert
        assertThat(transactionCreatedDTO.getAmount()).isNotNull();
        assertThat(transactionCreatedDTO.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(transactionCreatedDTO.getDescription()).isEqualTo(transaction.getDescription());
    }

    @Test
    void test_GetTransactions() {
        // Arrange
        String countryCurrency = "Canada-Dollar";
        String cacheKey = "Canada-Dollar2025-12-31";
        String DATE = "date";
        Date transactionDate = new Date();
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .date(transactionDate)
                .amount(BigDecimal.valueOf(123.56))
                .description("New Computer")
                .transactionId("TXN-1234567890")
                .exchangeRate(BigDecimal.valueOf(1.369))
                .convertedAmount(BigDecimal.valueOf(169.15))
                .countryCurrency(countryCurrency)
                .build();

        Transaction transaction = Transaction.builder()
                .date(transactionDate)
                .amount(BigDecimal.valueOf(123.56))
                .description("New Computer")
                .transactionId("TXN-1234567890")
                .build();

        ExchangeRateResponseDTO exchangeRateResponseDTO = new ExchangeRateResponseDTO();
        CurrencyExchangeRateDTO currencyExchangeRateDTO = CurrencyExchangeRateDTO.builder()
                .Exchange_Rate(BigDecimal.valueOf(1.369))
                .Record_Date(transactionDate)
                .Country_Currency_Desc(countryCurrency)
                .build();

        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();

        transactionDTOS.add(transactionDTO);
        transactions.add(transaction);
        exchangeRateResponseDTO.getData().add(currencyExchangeRateDTO);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(DATE).ascending());
        Page<TransactionDTO> transactionDTOPage = new PageImpl<>(transactionDTOS, pageable, transactionDTOS.size());
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
        doReturn(cacheKey).when(currencyRateCacheService).generateCacheKey(anyString(), eq(transactionDate));
        doReturn(exchangeRateResponseDTO).when(currencyRateCacheService).getCurrencyDateExchangeRates(countryCurrency, transactionDate, cacheKey);

        Page<TransactionDTO> result =
                transactionService.getTransactions(countryCurrency, 0, 10, DATE, true);

        assertThat(result).isEqualTo(transactionDTOPage);
        assertThat(result.getContent().getFirst().getConvertedAmount()).isEqualTo(BigDecimal.valueOf(169.15));
    }

    @Test
    void test_GetTransactionsRoundUp() {
        // Arrange
        String countryCurrency = "Canada-Dollar";
        String cacheKey = "Canada-Dollar2025-12-31";
        String DATE = "date";
        Date transactionDate = new Date();
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .date(transactionDate)
                .amount(BigDecimal.valueOf(123.56))
                .description("New Computer")
                .transactionId("TXN-1234567890")
                .exchangeRate(BigDecimal.valueOf(1.895))
                .convertedAmount(BigDecimal.valueOf(234.15))
                .countryCurrency(countryCurrency)
                .build();

        Transaction transaction = Transaction.builder()
                .date(transactionDate)
                .amount(BigDecimal.valueOf(123.56))
                .description("New Computer")
                .transactionId("TXN-1234567890")
                .build();

        ExchangeRateResponseDTO exchangeRateResponseDTO = new ExchangeRateResponseDTO();
        CurrencyExchangeRateDTO currencyExchangeRateDTO = CurrencyExchangeRateDTO.builder()
                .Exchange_Rate(BigDecimal.valueOf(1.895))
                .Record_Date(transactionDate)
                .Country_Currency_Desc(countryCurrency)
                .build();

        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();

        transactionDTOS.add(transactionDTO);
        transactions.add(transaction);
        exchangeRateResponseDTO.getData().add(currencyExchangeRateDTO);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(DATE).ascending());
        Page<TransactionDTO> transactionDTOPage = new PageImpl<>(transactionDTOS, pageable, transactionDTOS.size());
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
        doReturn(cacheKey).when(currencyRateCacheService).generateCacheKey(anyString(), eq(transactionDate));
        doReturn(exchangeRateResponseDTO).when(currencyRateCacheService).getCurrencyDateExchangeRates(countryCurrency, transactionDate, cacheKey);

        Page<TransactionDTO> result =
                transactionService.getTransactions(countryCurrency, 0, 10, DATE, true);

        assertThat(result.getContent().getFirst().getTransactionId()).isEqualTo(transactionDTOPage.getContent().getFirst().getTransactionId());
        assertThat(result.getContent().getFirst().getConvertedAmount()).isEqualTo(transactionDTOPage.getContent().getFirst().getConvertedAmount());
    }
}
