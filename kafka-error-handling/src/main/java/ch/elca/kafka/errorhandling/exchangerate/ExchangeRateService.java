package ch.elca.kafka.errorhandling.exchangerate;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final KafkaTemplate<String, ExchangeRate> kafkaTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void initExchangeRates() {
        ExchangeRate gbp = new ExchangeRate();
        gbp.setCurrency(Currency.getInstance("GBP"));
        gbp.setExchangeRates(Map.of(Currency.getInstance("CHF"), BigDecimal.valueOf(1.0952993)));
        kafkaTemplate.sendDefault(gbp.getCurrency().getCurrencyCode(), gbp);
    }
}
