package ch.elca.kafka.errorhandling.transfer.processing;

import ch.elca.kafka.errorhandling.exchangerate.ExchangeRate;
import ch.elca.kafka.errorhandling.transfer.Transfer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.binder.kafka.streams.DltPublishingContext;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.*;
import static java.math.BigDecimal.TWO;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransferProcessingConfigTest {

    public static final String PENDING_TRANSFER_ID = "123";
    public static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency CHF = Currency.getInstance("CHF");
    private final StringSerde keySerde = new StringSerde();
    private final JsonSerde<ExchangeRate> exchangeRateSerde = new JsonSerde<>(ExchangeRate.class);
    private final JsonSerde<Transfer> transferSerde = new JsonSerde<>(Transfer.class);
    private final JsonSerde<EnrichedTransfer> enrichedTransferSerde = new JsonSerde<>(EnrichedTransfer.class);
    private final TransferProcessingConfig transferProcessingConfig = new TransferProcessingConfig();
    private TopologyTestDriver testDriver;

    @Mock
    private DltPublishingContext dltPublishingContext;

    @BeforeEach
    public void setUp() {
        configureDeserializer(exchangeRateSerde.deserializer());
        configureDeserializer(transferSerde.deserializer());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Transfer> pendingTransfers = builder.stream(PENDING_TRANSFER_TOPIC, Consumed.with(keySerde, transferSerde));
        KTable<String, EnrichedTransfer>
                pendingTransfersDlt =
                builder.table(PENDING_TRANSFER_DLT_TOPIC, Consumed.with(keySerde, enrichedTransferSerde));
        GlobalKTable<String, ExchangeRate> exchangeRates = builder.globalTable(EXCHANGE_RATE_TOPIC, Materialized.with(keySerde, exchangeRateSerde));
        transferProcessingConfig.processPendingTransfer(dltPublishingContext)
                .apply(pendingTransfers)
                .apply(pendingTransfersDlt)
                .apply(exchangeRates)[0]
                .to(PROCESSED_TRANSFER_TOPIC);

        Topology topology = builder.build();
        testDriver = new TopologyTestDriver(topology, getStreamsConfiguration());
    }

    @Test
    void localCurrencyShouldBeSet() {
        TestInputTopic<String, Transfer> pendingTransferTopic = testDriver
                .createInputTopic(PENDING_TRANSFER_TOPIC, keySerde.serializer(), transferSerde.serializer());
        TestInputTopic<String, ExchangeRate> exchangeRateTopic = testDriver
                .createInputTopic(EXCHANGE_RATE_TOPIC, keySerde.serializer(), exchangeRateSerde.serializer());
        TestOutputTopic<String, EnrichedTransfer> outputTopic = testDriver
                .createOutputTopic(PROCESSED_TRANSFER_TOPIC, keySerde.deserializer(), enrichedTransferSerde.deserializer());

        ExchangeRate gbp = new ExchangeRate();
        gbp.setCurrency(GBP);
        gbp.setExchangeRates(Map.of(CHF, BigDecimal.valueOf(1.12)));
        exchangeRateTopic.pipeInput(GBP.getCurrencyCode(), gbp);

        Transfer pendingTransfer = new Transfer();
        pendingTransfer.setId(PENDING_TRANSFER_ID);
        pendingTransfer.setCurrency(GBP.getCurrencyCode());
        pendingTransfer.setAmount(TWO);
        pendingTransferTopic.pipeInput(PENDING_TRANSFER_ID, pendingTransfer);

        assertThat(outputTopic.readKeyValue().value.transfer().getAmountInLocalCurrency()).isEqualByComparingTo("2.24");
    }

    private Properties getStreamsConfiguration() {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde.class);
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        return streamsConfiguration;
    }

    private void configureDeserializer(Deserializer<?> deserializer) {
        Map<String, Object> deserializerConfig = new HashMap<>();
        deserializerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        deserializer.configure(deserializerConfig, false);
    }
}
