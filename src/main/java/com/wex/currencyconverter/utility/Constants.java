package com.wex.currencyconverter.utility;

public class Constants {
    public static final int THREE = 3;
    public static final int SIX = 6;
    public static final int NINE = 9;
    public static final int TWELVE = 12;
    public static final int THIRTY = 30;
    public static final int THIRTY_ONE = 31;
    public static final int MONTHS_TO_SUBTRACT = 3;
    public static final int RANDOM_NUMBER_LOWER = 10000;
    public static final int RANDOM_NUMBER_UPPER = 99999;
    public static final String LASTDAYOFMARCH = "-03-31";
    public static final String LASTDAYOFJUNE = "-06-30";
    public static final String LASTDAYOFSEPTEMBER = "-09-30";
    public static final String LASTDAYOFDECEMBER = "-12-31";
    public static final String ERRORS = "errors";
    public static final String TRANSACTION_PREFIX = "TXN-";

    public static final String FISCALDATA_BASE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";
    public static final String FISCALDATA_PARAMS_URL= "?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:\" +\n" +
            "                \"({currency}),record_date:gte:{lowDate},record_date:lte:{highDate}";

    public static final String FISCALDATA_ERROR1 = "Error occurred while fetching data from Fiscal Service. Response status:";
    public static final String FISCALDATA_ERROR2 = ", body: ";

    // Statics for I18N and L10N
    public static final String TRANSACTION_DATE_COUNTRY_CURRENTY = "transaction.date.country.currency";
    public static final String TRANSACTION_CURRENCY_NOT_CONVERTED = "transaction.currency.not.converted";
    public static final String TRANSACTION_DATE_RANGE = "transaction.date.range";
    public static final String ERROR_HTTP_RETRIEVE = "error.http.retrieve";

    // Validations
    public static final String DOLLAR_FORMAT = "0,000.00";
    public static final String DATE_FORMAT = "YYYY-MM-dd";
    public static final String COUNTRY_CURRENCY_DESC = "country_currency_desc";
    public static final String EXCHANGE_RATE = "exchange_rate";
    public static final String RECORD_DATE = "record_date";

}
