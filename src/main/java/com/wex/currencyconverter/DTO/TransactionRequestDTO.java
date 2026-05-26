package com.wex.currencyconverter.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "{dollar.decimal.blank}")
    @DecimalMin(value = "0.00", inclusive = false, message = "{dollar.amount.greater.zero}")
    @Digits(integer = 16, fraction = 2, message = "{dollar.amount.precision}")
    private BigDecimal amount;

    @NotNull(message = "{date.required}")
    @Past(message = "{date.past}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;

    @NotBlank(message = "{description.required}")
    @NotNull(message = "{description.required}")
    @Length(min = 1, max = 50, message = "{description.length}")
    private String description;
}
