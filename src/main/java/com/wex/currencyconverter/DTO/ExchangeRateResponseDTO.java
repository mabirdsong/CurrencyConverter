package com.wex.currencyconverter.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRateResponseDTO {
    @JsonProperty("data")
    List<CurrencyExchangeRateDTO> data = new ArrayList<>();
}