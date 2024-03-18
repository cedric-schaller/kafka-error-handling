package ch.elca.kafka.errorhandling.transfer.pending;

import ch.elca.kafka.errorhandling.transfer.TransferDto;
import ch.elca.kafka.errorhandling.transfer.TransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class PendingTransferController {

    private final PendingTransferService service;
    private final TransferMapper mapper;

    @PutMapping("/transfer/{id}")
    public void sendTransfer(@PathVariable String id, @RequestBody TransferDto pendingTransfer) throws ExecutionException, InterruptedException {
        service.process(mapper.toTransfer(pendingTransfer));
    }
}
