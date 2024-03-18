CREATE TABLE transfer
(
    id                       VARCHAR(36) PRIMARY KEY,
    datetime                 TIMESTAMP,
    iban_from                VARCHAR(24),
    iban_to                  VARCHAR(24),
    amount                   NUMERIC(20, 2),
    currency                 VARCHAR(3),
    amount_in_local_currency NUMERIC(20, 2),
    status                   VARCHAR(10)
);

CREATE TABLE account
(
    id      BIGINT AUTO_INCREMENT,
    iban    VARCHAR(24),
    balance NUMERIC(20, 2)
);

INSERT INTO account (iban, balance)
VALUES ('CH5604835012345678009', 10000000)