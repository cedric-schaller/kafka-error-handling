package ch.elca.kafka.errorhandling.thirdparty.transfer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransferController {

    private final boolean shouldFail = false;

    @PutMapping("/transfer/{id}")
    public void sendTransfer(@PathVariable String id, @RequestBody TransferDto transferDto) {
        if (shouldFail) {
            throw new IllegalStateException();
        } else {
            log.info("Transfer {} settled by third party.", transferDto.getId());
        }
    }
}
