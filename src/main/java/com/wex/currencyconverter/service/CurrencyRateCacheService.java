package com.wex.currencyconverter.service;

import com.wex.currencyconverter.DTO.ExchangeRateResponseDTO;
import com.wex.currencyconverter.utility.Constants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateCacheService {

    private final RestClient restClient;
    private final MessageSource messageSource;

    /**
     * Retrieves the exchange rate based on the country's currency and the transaction date.
     * @param countryCurrency Code representing the currency for a country
     * @param transactionDate Date of the Transaction
     * @param cacheKey Key used to query the cache for exchangerateresponsedto objects.  If in cache, it will not
     *                 make the API call.  If not in cache, the API call will be executed.
     * @return An exchange object containing the exchange rate(s) base on the country currency code and transaction date.
     */
    @Cacheable(value = "exchangerateresponsedto", key = "#cacheKey")
    public ExchangeRateResponseDTO getCurrencyDateExchangeRates(String countryCurrency, Date transactionDate,
                                                                String cacheKey) {
        log.info("Making API call to get currency exchange rates and cache key: {}",  cacheKey);
        ExchangeRateResponseDTO exchangeRateResponseDTO = new ExchangeRateResponseDTO();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);
        LocalDate transactionlocalDate = localDateTime.toLocalDate();
        LocalDate tranactionMinusThreelocalDate = transactionlocalDate.minusMonths(Constants.MONTHS_TO_SUBTRACT);

        exchangeRateResponseDTO = callConvertionRatesAPI(countryCurrency, transactionDate, transactionlocalDate,
                tranactionMinusThreelocalDate);

        if (exchangeRateResponseDTO.getData() == null || exchangeRateResponseDTO.getData().isEmpty()) {
            tranactionMinusThreelocalDate = tranactionMinusThreelocalDate.minusMonths(Constants.MONTHS_TO_SUBTRACT);
            exchangeRateResponseDTO = callConvertionRatesAPI(countryCurrency, transactionDate, transactionlocalDate,
                    tranactionMinusThreelocalDate);

            if (exchangeRateResponseDTO.getData() == null || exchangeRateResponseDTO.getData().isEmpty()) {
                String error = messageSource.getMessage(Constants.TRANSACTION_DATE_RANGE,
                        List.of(localDateTime.toLocalDate()).toArray(),
                        Locale.US);
                String errorMessage = messageSource.getMessage(Constants.TRANSACTION_CURRENCY_NOT_CONVERTED,
                        List.of(localDateTime.toLocalDate()).toArray(),
                        Locale.US);
                log.warn(error);
                /*throw new ApiCallException(HttpStatus.UNPROCESSABLE_CONTENT, error,
                        List.of(error, errorMessage));*/
            }
        }

        return exchangeRateResponseDTO;
    }

    /**
     * Retrieves the exchange rate based on the country's currency and the transaction date.  This rate is retrieved
     * from a Fiscal Data API call.
     * @param countryCurrency Code representing the currency for a country
     * @param transactionDate Date of the Transaction
     * @param transactionlocalDate Transaction date in LocalDate format.
     * @param tranactionMinusThreeOrSixlocalDate Transaction date in LocalDate format minus three or six months.
     * @return
     */
    protected ExchangeRateResponseDTO callConvertionRatesAPI(String countryCurrency, Date transactionDate,
                                                   LocalDate transactionlocalDate,
                                                   LocalDate tranactionMinusThreeOrSixlocalDate) {
        String URL = "?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:" +
                "({currency}),record_date:gte:{lowDate},record_date:lte:{highDate}";

        return restClient.get()
                         .uri(URL, countryCurrency, tranactionMinusThreeOrSixlocalDate.toString(),
                                transactionlocalDate.toString())
                         .retrieve()
                         .body(new ParameterizedTypeReference<ExchangeRateResponseDTO>() {});
    }

    /**
     * Generates a Key used for caching ExchangeRateResponseDTO objects.
     * @param countryCurrency Country currency identifier
     * @param transactionDate Date of the transaction
     * @return Return a key used to cache ExchangeRateResponseDTO objects
     */
    public String generateCacheKey(String countryCurrency, Date transactionDate) {
        String cacheKey = "";

        LocalDateTime localDateTime = LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneOffset.UTC);
        LocalDate transactionlocalDate = localDateTime.toLocalDate();
        LocalDate tranactionMinusThreelocalDate = transactionlocalDate.minusMonths(Constants.MONTHS_TO_SUBTRACT);

        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        if (day == Constants.THIRTY || day == Constants.THIRTY_ONE) {
            if( (month == Constants.SIX || month == Constants.NINE) && day == Constants.THIRTY) {
                cacheKey = countryCurrency + transactionlocalDate;
            } else if ( (month == Constants.THREE || month == Constants.TWELVE) && day == Constants.THIRTY_ONE) {
                cacheKey = countryCurrency + transactionlocalDate;
            }
        } else {
            int monthMinusThree = tranactionMinusThreelocalDate.getMonthValue();
            if (monthMinusThree <= Constants.THREE) {
                cacheKey = countryCurrency + tranactionMinusThreelocalDate.getYear() + Constants.LASTDAYOFMARCH;
            }  else if (monthMinusThree <= Constants.SIX) {
                cacheKey = countryCurrency + tranactionMinusThreelocalDate.getYear() + Constants.LASTDAYOFJUNE;
            } else if (monthMinusThree <= Constants.NINE) {
                cacheKey = countryCurrency + tranactionMinusThreelocalDate.getYear() + Constants.LASTDAYOFSEPTEMBER;
            }  else {
                cacheKey = countryCurrency + tranactionMinusThreelocalDate.getYear() + Constants.LASTDAYOFDECEMBER;
            }
        }

        return cacheKey;
    }
}
