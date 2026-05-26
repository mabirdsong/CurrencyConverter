package com.wex.currencyconverter.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wex.currencyconverter.utility.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeRateDTO {
    @JsonProperty(Constants.COUNTRY_CURRENCY_DESC)
    private String Country_Currency_Desc;

    @JsonProperty(Constants.EXCHANGE_RATE)
    private BigDecimal Exchange_Rate;

    @JsonProperty(Constants.RECORD_DATE)
    private Date Record_Date;
}
