package com.wex.currencyconverter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wex.currencyconverter.model.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
