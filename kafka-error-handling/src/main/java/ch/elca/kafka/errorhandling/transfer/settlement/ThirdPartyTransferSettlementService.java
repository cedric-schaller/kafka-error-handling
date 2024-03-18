package ch.elca.kafka.errorhandling.transfer.settlement;

import ch.elca.kafka.errorhandling.transfer.Transfer;
import ch.elca.kafka.errorhandling.transfer.TransferDto;
import ch.elca.kafka.errorhandling.transfer.TransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.PROCESSED_TRANSFER_TOPIC;

@Service
@RequiredArgsConstructor
public class ThirdPartyTransferSettlementService {

    private final RestTemplate restTemplate;
    private final TransferMapper mapper;

    @KafkaListener(groupId = "ThirdPartyTransferSettlementService", topics = PROCESSED_TRANSFER_TOPIC)
    public void settle(Transfer transfer) {
        TransferDto transferDto = mapper.toTransferDto(transfer);
        restTemplate.put("http://localhost:8081/transfer/" + transfer.getId(), transferDto);
    }
}
