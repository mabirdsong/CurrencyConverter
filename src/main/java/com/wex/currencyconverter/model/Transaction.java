package com.wex.currencyconverter.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Purchase_Amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(name = "Transaction_Date",  nullable = false)
    private Date date;

    @Column(name = "Transaction_Description", nullable = false, length = 50)
    private String description;

    @Column(name = "Transaction_Identifier", nullable = false, length = 50)
    private String transactionId;
}
