package ch.elca.kafka.errorhandling.transfer.pending;

import ch.elca.kafka.errorhandling.transfer.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log
public class PendingTransferService {

    private final KafkaTemplate<String, Transfer> kafkaTemplate;
    private final TransferRepository repository;

    public void process(Transfer pendingTransfer) {
        // TODO: call .get();
        kafkaTemplate.sendDefault(pendingTransfer.getId(), pendingTransfer);
        repository.save(pendingTransfer);
    }
}
