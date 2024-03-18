package ch.elca.kafka.errorhandling.exchangerate;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

@Data
public class ExchangeRate {
    Currency currency;

    Map<Currency, BigDecimal> exchangeRates;
}
