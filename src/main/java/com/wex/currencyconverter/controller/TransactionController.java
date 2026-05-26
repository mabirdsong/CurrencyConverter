package com.wex.currencyconverter.controller;

import com.wex.currencyconverter.DTO.ApiResponse;
import com.wex.currencyconverter.DTO.TransactionCreatedDTO;
import com.wex.currencyconverter.DTO.TransactionDTO;
import com.wex.currencyconverter.DTO.TransactionRequestDTO;
import com.wex.currencyconverter.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction Controller", description = "Operations related to transaction dollar conversion and management")
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping("/new")
    @Operation(summary = "Creates a new transaction", description = "Creates and stores a new transaction that will be uses for exchange rate conversion.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Succesfully create a new transaction",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request, missing field, or field validation failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server processing error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        TransactionCreatedDTO transactionDTO = transactionService.create(transactionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Transaction created successfully", transactionDTO));
    }

    @GetMapping("/listconversionrates/countrycurrency/{countryCurrency}")
    @Operation(summary = "Retrieve all transactions", description = "Retrieve all transaction with the amount converted using a counties exchange rate.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Succesfully retreived all transaction",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request, missing field, or field validation failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server processing error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    public ResponseEntity<ApiResponse> listConversionRates(@PathVariable String countryCurrency,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "date") String sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending) {
        Page<TransactionDTO> transactionDTOList = transactionService.getTransactions(countryCurrency, page, size,
                                                                                     sortBy, ascending);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Transactions found", transactionDTOList));
    }
}
