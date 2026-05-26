package com.wex.currencyconverter.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallException extends RuntimeException {
    private HttpStatus httpStatus;
    private String errorMessage;
    private List<String> errorList;
}
