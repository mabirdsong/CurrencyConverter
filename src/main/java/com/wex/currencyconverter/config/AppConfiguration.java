package com.wex.currencyconverter.config;

import com.wex.currencyconverter.exceptions.ApiCallException;
import com.wex.currencyconverter.utility.Constants;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.boot.restclient.autoconfigure.RestClientSsl;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Slf4j
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AppConfiguration {

    private final MessageSource messageSource;

    public AppConfiguration(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public RestClient customSSLRestClient(RestClient.Builder builder, RestClientSsl restClientSsl) {
        return builder
                .baseUrl(Constants.FISCALDATA_BASE_URL)
                .apply(restClientSsl.fromBundle("my-bundle"))
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
                    String error = Constants.FISCALDATA_ERROR1 + res.getStatusCode()
                            + Constants.FISCALDATA_ERROR2 + res.getStatusText();
                    log.error(error);
                    throw new ApiCallException(HttpStatus.resolve(res.getStatusCode().value()), res.getStatusText(),
                                                                  List.of(res.getStatusText(),
                                                                  messageSource.getMessage(Constants.ERROR_HTTP_RETRIEVE,
                                                                                  null,
                                                                                  LocaleContextHolder.getLocale())));
                })
                .build();
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        // Set a default fallback language if the header is missing
        resolver.setDefaultLocale(Locale.US);
        // Restrict to supported languages (optional but recommended)
        resolver.setSupportedLocales(List.of(Locale.US, Locale.FRANCE,  Locale.GERMANY));
        return resolver;
    }
}
