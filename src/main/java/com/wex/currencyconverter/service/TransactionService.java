package com.wex.currencyconverter.service;

import com.wex.currencyconverter.DTO.ExchangeRateResponseDTO;
import com.wex.currencyconverter.DTO.CurrencyExchangeRateDTO;
import com.wex.currencyconverter.DTO.TransactionCreatedDTO;
import com.wex.currencyconverter.DTO.TransactionRequestDTO;
import com.wex.currencyconverter.DTO.TransactionDTO;
import com.wex.currencyconverter.model.Transaction;
import com.wex.currencyconverter.repository.TransactionRepository;
import com.wex.currencyconverter.utility.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final SecureRandom random;
    private final CurrencyRateCacheService currencyRateCacheService;
    private final MessageSource messageSource;

    /**
     * Adds a transaction to the database.
     * @param transactionRequestDTO Object containing the data need to create a transaction in the database.
     * @return A transaction object create in the database.
     */
    @Override
    @Transactional
    public TransactionCreatedDTO create(TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = Transaction.builder()
                .date(transactionRequestDTO.getDate())
                .description(transactionRequestDTO.getDescription())
                .amount(transactionRequestDTO.getAmount())
                .transactionId(generateTransactionId())
                .build();
        transactionRepository.save(transaction);
        return convertToCreatedDTO(transaction);
    }

    /**
     * Retrieves all transactions from the database.
     * @param countryCurrency Country + Currency identifier used to retrieve exchange rate.
     * @param page The current page.
     * @param size The size of a page.
     * @return A collection of transaction DTO objects with the US Dollar amount converted to the currency provided.
     */
    @Override
    public Page<TransactionDTO> getTransactions(String countryCurrency,
                                                int page, int size, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactionList = transactionRepository.findAll(pageable);
        return createDTOs(transactionList, countryCurrency, pageable);
    }

    /**
     * Coverts a transaction object to a transaction DTO object using ModelMapper.
     * @param transaction Transaction that is converted to a transaction DTO object.
     * @return Transaction DTO object.
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDTO.class);
    }

    /**
     * Coverts a transaction object to a transaction created DTO object using ModelMapper.
     * @param transaction Transaction that is converted to a transaction DTO object.
     * @return Transaction DTO object.
     */
    private TransactionCreatedDTO convertToCreatedDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionCreatedDTO.class);
    }

    /**
     * Creates a transaction ID based on the system time
     * @return Generated Transaction ID.
     */
    private String generateTransactionId() {
        return Constants.TRANSACTION_PREFIX +
               random.nextInt(Constants.RANDOM_NUMBER_LOWER, Constants.RANDOM_NUMBER_UPPER) +
               System.currentTimeMillis();
    }

    /**
     * Converts each database transaction to an DTO object.  Calculates the conversion amount base on the transactions
     * rate for the country provided and the date of the transaction.
     * @param transactionList List of transactions retrieved from the database.
     * @param countryCurrency Country currency identification.
     * @return A list of transaction DTO objects.
     */
    private Page<TransactionDTO> createDTOs(Page<Transaction> transactionList, String countryCurrency, Pageable pageable) {
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        for(Transaction transaction : transactionList) {
            String cacheKey = currencyRateCacheService.generateCacheKey(countryCurrency, transaction.getDate());
            log.info("Cache key: {}", cacheKey);
            ExchangeRateResponseDTO exchangeRateResponseDTO =
                    currencyRateCacheService.getCurrencyDateExchangeRates(countryCurrency,
                                                                          transaction.getDate(),
                                                                          cacheKey);
            TransactionDTO transactionDTO = convertToDTO(transaction);
            convertToCurrency(transactionDTO, exchangeRateResponseDTO, countryCurrency);
            transactionDTOS.add(transactionDTO);
        }
        return new PageImpl<>(transactionDTOS, pageable, transactionList.getTotalElements());
    }

    /**
     * Fills in the remaining values (convertedAmount, exchange_rate, and country currency) on the transaction DTO object.
     * The calculateAmount is calculated and round while maintaining precision.
     * @param transactionDTO DTO object containing transaction data.
     * @param exchangeRateResponseDTO Country currency exchange rate retrieved from API call.
     * @param countryCurrency Country currency identification.
     */
    private void convertToCurrency(TransactionDTO transactionDTO, ExchangeRateResponseDTO exchangeRateResponseDTO,
                                   String countryCurrency) {
        BigDecimal amount = transactionDTO.getAmount();
        List<CurrencyExchangeRateDTO> currencyExchangeRateDTOList = exchangeRateResponseDTO.getData();
        if(currencyExchangeRateDTOList != null && !currencyExchangeRateDTOList.isEmpty()) {
            CurrencyExchangeRateDTO currencyExchangeRateDTO = currencyExchangeRateDTOList.getFirst();
            transactionDTO.setConvertedAmount(round(amount.multiply(currencyExchangeRateDTO.getExchange_Rate())));
            transactionDTO.setExchangeRate(currencyExchangeRateDTO.getExchange_Rate());
            transactionDTO.setCountryCurrency(currencyExchangeRateDTO.getCountry_Currency_Desc());
        } else {
            transactionDTO.setConvertedAmount(BigDecimal.ZERO);
            transactionDTO.setExchangeRate(BigDecimal.ZERO);
            transactionDTO.setCountryCurrency(countryCurrency);
            transactionDTO.setErrorStatus(HttpStatus.NOT_FOUND.value());
            transactionDTO.setErrorMessage(messageSource.getMessage(Constants.TRANSACTION_DATE_COUNTRY_CURRENTY, null,
                    LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Rounds the value provided using the HALF_UP rule.
     * @param value Value to round up.
     * @return Value round up using the HALF_UP rule maintaining two decimal precision.
     */
    private BigDecimal round(BigDecimal value) {
        int DECIMALS_PRECISION = 2;
        return value.setScale(DECIMALS_PRECISION, RoundingMode.HALF_UP);
    }
}
