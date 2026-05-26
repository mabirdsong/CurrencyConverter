package com.wex.currencyconverter.repository;

import com.wex.currencyconverter.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootTest
public class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void save_TransactionTest() {

        // Act
        Transaction transaction = transactionRepository.save(Transaction.builder()
                .date(new Date())
                .description("Christmas Gift")
                .amount(BigDecimal.valueOf(150.38))
                .transactionId("TXN-156489735")
                .build());

        // Assert
        assertThat(transactionRepository.findAll()).hasSize(1);
        assertThat(transactionRepository.findAll().get(0).getDescription()).isEqualTo("Christmas Gift");
        assertThat(transactionRepository.findAll().get(0).getAmount()).isEqualTo(BigDecimal.valueOf(150.38));
        assertThat(transactionRepository.findAll().get(0).getTransactionId()).isEqualTo("TXN-156489735");
    }
}
