package ch.elca.kafka.errorhandling.config;

import ch.elca.kafka.errorhandling.exchangerate.ExchangeRate;
import ch.elca.kafka.errorhandling.transfer.Transfer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.EXCHANGE_RATE_TOPIC;
import static ch.elca.kafka.errorhandling.config.TopicsConfig.PENDING_TRANSFER_TOPIC;

@Configuration
public class KafkaConfig {

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }

    @Bean
    public ProducerFactory<String, Transfer> pendingTransferProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, ExchangeRate> exchangeRateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Transfer> pendingTransferKafkaTemplate() {
        var kafkaTemplate = new KafkaTemplate<>(pendingTransferProducerFactory(), producerConfigs());
        kafkaTemplate.setDefaultTopic(PENDING_TRANSFER_TOPIC);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, ExchangeRate> exchangeRateKafkaTemplate() {
        var kafkaTemplate = new KafkaTemplate<>(exchangeRateProducerFactory(), producerConfigs());
        kafkaTemplate.setDefaultTopic(EXCHANGE_RATE_TOPIC);
        return kafkaTemplate;
    }
}
