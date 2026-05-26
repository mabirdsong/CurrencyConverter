package com.wex.currencyconverter.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TransactionCreatedDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DOLLAR_FORMAT)
    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private Date date;
    private String description;
    private String transactionId;
}
