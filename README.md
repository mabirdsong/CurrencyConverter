# CurrencyConverter

A backend currency converter app with a **Java Spring Boot** backend.

---

## Architecture

```
currencyconverter/  →  Java Spring Boot + H2 DB (port 8081)
```

---

## Quick Start

### 1. Start the Java backend

Requires: **Java 21+**, **Maven**

```bash
cd currencyconverter
mvn spring-boot:run
```
```
Using IDE:
Load project into Intellij.
Run CurrencycoverterApplication 
```

```
There is a HTTPS request to retriev exchange rate data.
A root certificate chain is needed to complete this request.
The certificate is located here: ./currencyconverter/src/main/resources/static/fiscaldata.treasury.gov.pem.
Add this certificate chain to your cacert file using the java Keytool or OpenSSl before running this application.
```

The server starts on `http://localhost:8081`.  
H2 console available at `http://localhost:8081/h2` (JDBC URL: `jdbc:h2:mem:transactionsdb`).

## Features

| Feature         | Description                                     |
|-----------------|-------------------------------------------------|
| **New**         | Adds a new transaction.                         |
| **List**        | Lists all transactions with currency converted. |

---

## API Reference

| Method | Path                          | Description                                                                 |
|--------|-------------------------------|-----------------------------------------------------------------------------|
| Post   | `/api/v1/transaction/new`     | Adds a new transacation                                                     |
| GET    | `/api/v1/transaction/listconversionrates/countrycurrency/{countryCurrency}`           | Get all transactions. Standard pagination is availalbe for large data sets. |

---

## API Examples
| Method | Action            | HTTP Call                                                                                                                             |
|--------|-------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| POST   | Add A Transaction | http://localhost:8081/api/v1/transaction/new with a JSON Body of  {"description" : "Sample Transaction 2","amount" : "559.80","date" : "2018-02-11"} |
| GET    | List Transactions | `http://localhost:8081/api/v1/transaction/listconversionrates/countrycurrency/Canada-Dollar?page=0&size=1&sortby=date&ascending=true` |
