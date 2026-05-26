package com.wex.currencyconverter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
		info = @Info(
				title = "Transaction Currency Converter",
				version = "1.0",
				description = "Documentation for Transaction Currency Conversion and retrieval endpoints"
		)
)
public class CurrencyconverterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyconverterApplication.class, args);
	}

}
