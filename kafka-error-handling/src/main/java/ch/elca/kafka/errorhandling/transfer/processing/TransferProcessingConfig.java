package ch.elca.kafka.errorhandling.transfer.processing;

import ch.elca.kafka.errorhandling.exchangerate.ExchangeRate;
import ch.elca.kafka.errorhandling.transfer.Transfer;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Currency;
import java.util.function.BiFunction;

@Configuration
public class TransferProcessingConfig {

    private static final Currency CHF = Currency.getInstance("CHF");

    @Bean
    public BiFunction<KStream<String, Transfer>, GlobalKTable<String, ExchangeRate>, KStream<String, Transfer>> processPendingTransfer() {
        KeyValueMapper<String, Transfer, String> byCurrency = (id, pendingTransfer) -> pendingTransfer.getCurrency();

        return (pendingTransfers, exchangeRatesTable) -> pendingTransfers
                .leftJoin(exchangeRatesTable, byCurrency, this::processTransfer);
    }

    private Transfer processTransfer(Transfer pendingTransfer, ExchangeRate exchangeRate) {
        pendingTransfer.setAmountInLocalCurrency(pendingTransfer.getAmount().multiply(exchangeRate.getExchangeRates().get(CHF)));
        return pendingTransfer;
    }
}
