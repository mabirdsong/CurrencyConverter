package com.wex.currencyconverter.service;

import com.wex.currencyconverter.DTO.TransactionCreatedDTO;
import com.wex.currencyconverter.DTO.TransactionDTO;
import com.wex.currencyconverter.DTO.TransactionRequestDTO;
import org.springframework.data.domain.Page;


public interface ITransactionService {

    public TransactionCreatedDTO create(TransactionRequestDTO transaction);
    Page<TransactionDTO> getTransactions(String countryCurrency,
                                         int page, int size, String sortBy, boolean ascending);
}