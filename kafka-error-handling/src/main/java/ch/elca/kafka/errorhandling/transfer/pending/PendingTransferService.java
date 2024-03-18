package ch.elca.kafka.errorhandling.transfer.pending;

import ch.elca.kafka.errorhandling.transfer.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Log
public class PendingTransferService {

    private final KafkaTemplate<String, Transfer> kafkaTemplate;
    private final TransferRepository repository;

    @Transactional("transactionManager")
    public void process(Transfer pendingTransfer) throws ExecutionException, InterruptedException {
        kafkaTemplate.sendDefault(pendingTransfer.getIbanFrom(), pendingTransfer).get();
        repository.save(pendingTransfer);
    }
}
