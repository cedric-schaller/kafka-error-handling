package ch.elca.kafka.errorhandling.transfer.processing;

import ch.elca.kafka.errorhandling.exchangerate.ExchangeRate;
import ch.elca.kafka.errorhandling.transfer.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.api.Record;
import org.springframework.cloud.stream.binder.kafka.streams.DltAwareProcessor;
import org.springframework.cloud.stream.binder.kafka.streams.DltPublishingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.function.Function;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.PENDING_TRANSFER_DLT_TOPIC;
import static ch.elca.kafka.errorhandling.transfer.TransferStatus.PROCESSED;

@Configuration
@Slf4j
public class TransferProcessingConfig {

    private static final Currency CHF = Currency.getInstance("CHF");
    private static final KeyValueMapper<String, EnrichedTransfer, String>
            byCurrency =
            (id, pendingTransfer) -> pendingTransfer.transfer().getCurrency();

    private static Predicate<String, EnrichedTransfer> isIbanFromInDlt() {
        return (key, value) -> value.dltTransfer() != null;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Function<KStream<String, Transfer>,
            Function<KTable<String, EnrichedTransfer>,
                    Function<GlobalKTable<String, ExchangeRate>, KStream<String, EnrichedTransfer>[]>>>
    processPendingTransfer(DltPublishingContext dltPublishingContext) {

        return pendingTransfer ->
                pendingTransferDlt ->
                        exchangeRatesTable -> {
                            Map<String, KStream<String, EnrichedTransfer>> streams = pendingTransfer
                                    .leftJoin(pendingTransferDlt, this::enrichTransfer)
                                    .leftJoin(exchangeRatesTable, byCurrency, this::enrichTransfer)
                                    .split(Named.as("branch-"))
                                    .branch(isIbanFromInDlt(), Branched.as("ibanFrom-contained-in-dlt"))
                                    .defaultBranch(Branched.as("process-transfer"));

                            KStream<String, EnrichedTransfer> sendToDltStream = streams.get("branch-ibanFrom-contained-in-dlt");
                            KStream<String, EnrichedTransfer> processTransactionsStream = streams.get("branch-process-transfer")
                                    // replacement for:
                                    // .mapValues(this::toTransfer);
                                    // see https://docs.spring.io/spring-cloud-stream/reference/kafka/kafka-streams-binder/error-handling.html#runtime-error-handling
                                    .process(() -> new DltAwareProcessor<>(this::processTransfer,
                                            PENDING_TRANSFER_DLT_TOPIC,
                                            dltPublishingContext));
                            return new KStream[]{processTransactionsStream, sendToDltStream};
                        };
    }

    private EnrichedTransfer enrichTransfer(Transfer pendingTransfer, EnrichedTransfer dltTransfer) {
        log.info("Joining with DLT");
        return new EnrichedTransfer(pendingTransfer, dltTransfer, null);
    }

    private EnrichedTransfer enrichTransfer(EnrichedTransfer enrichedTransfer, ExchangeRate exchangeRate) {
        log.info("Joining with exchange rate");
        return new EnrichedTransfer(enrichedTransfer.transfer(), enrichedTransfer.dltTransfer(), exchangeRate);
    }

    private Record<String, EnrichedTransfer> processTransfer(Record<String, EnrichedTransfer> enrichedTransferRecord) {
        log.info("Computing the amount in local currency");
        Transfer transfer = enrichedTransferRecord.value().transfer();
        BigDecimal exchangeRate = enrichedTransferRecord.value().exchangeRate().getExchangeRates().get(CHF);
        transfer.setAmountInLocalCurrency(transfer.getAmount().multiply(exchangeRate));
        transfer.setStatus(PROCESSED);
        return new Record<>(enrichedTransferRecord.key(), enrichedTransferRecord.value(), enrichedTransferRecord.timestamp());
    }
}
