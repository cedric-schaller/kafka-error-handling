package ch.elca.kafka.errorhandling.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicsConfig {

    public static final String PENDING_TRANSFER_TOPIC = "pending-transfer";
    public static final String EXCHANGE_RATE_TOPIC = "exchange-rate";
    public static final String PROCESSED_TRANSFER_TOPIC = "processed-transfer";
    public static final String PENDING_TRANSFER_DLT_TOPIC = "pending-transfer-dlt";

    @Bean
    public NewTopic pendingTransfer() {
        return TopicBuilder.name(PENDING_TRANSFER_TOPIC)
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic pendingTransferDlt() {
        return TopicBuilder.name(PENDING_TRANSFER_DLT_TOPIC)
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic exchangeRate() {
        return TopicBuilder.name(EXCHANGE_RATE_TOPIC)
                .partitions(12)
                .compact()
                .build();
    }

    @Bean
    public NewTopic processedTransfer() {
        return TopicBuilder.name(PROCESSED_TRANSFER_TOPIC)
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic processedTransferRetry1000() {
        return TopicBuilder.name("processed-transfer-retry-1000")
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic processedTransferRetry2000() {
        return TopicBuilder.name("processed-transfer-retry-2000")
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic processedTransferRetry4000() {
        return TopicBuilder.name("processed-transfer-retry-4000")
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic processedTransferRetry8000() {
        return TopicBuilder.name("processed-transfer-retry-8000")
                .partitions(12)
                .build();
    }

    @Bean
    public NewTopic processedTransferDlt() {
        return TopicBuilder.name("processed-transfer-dlt")
                .partitions(12)
                .build();
    }
}
