package com.wex.currencyconverter.exceptions;

import com.wex.currencyconverter.utility.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());

        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, List<String>>> handleJSONParseException(HttpMessageNotReadableException exception) {
        String error = exception.getMessage();
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ApiCallException.class)
    public ResponseEntity<Map<String, List<String>>> handleApiCallException(ApiCallException exception) {
        String error = exception.getErrorMessage();
        List<String> errors = exception.getErrorList();
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), exception.getHttpStatus());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleRuntimeException(Exception exception) {
        String error = exception.getMessage();
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put(Constants.ERRORS, errors);
        return errorResponse;
    }
}
