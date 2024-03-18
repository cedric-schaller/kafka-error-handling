package ch.elca.kafka.errorhandling.transfer.settlement;

import ch.elca.kafka.errorhandling.transfer.Transfer;
import ch.elca.kafka.errorhandling.transfer.TransferDto;
import ch.elca.kafka.errorhandling.transfer.TransferMapper;
import ch.elca.kafka.errorhandling.transfer.processing.EnrichedTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.PROCESSED_TRANSFER_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyTransferSettlementService {

    private final RestTemplate restTemplate;
    private final TransferMapper mapper;

    @RetryableTopic(attempts = "5", backoff = @Backoff(multiplier = 2.0))
    @KafkaListener(groupId = "ThirdPartyTransferSettlementService", topics = PROCESSED_TRANSFER_TOPIC)
    public void settle(EnrichedTransfer enrichedTransfer) {
        Transfer transfer = enrichedTransfer.transfer();
        TransferDto transferDto = mapper.toTransferDto(transfer);
        restTemplate.put("http://localhost:8081/transfer/" + transfer.getId(), transferDto);
    }

    @DltHandler
    public void handleDltMessage(EnrichedTransfer enrichedTransfer) {
        log.error("All retries have failed for transfer {}. A compensation mechanism should be implemented. ", enrichedTransfer.transfer().getId());
    }
}
