package ch.elca.kafka.errorhandling.transfer.settlement;

import ch.elca.kafka.errorhandling.account.Account;
import ch.elca.kafka.errorhandling.account.AccountRepository;
import ch.elca.kafka.errorhandling.transfer.Transfer;
import ch.elca.kafka.errorhandling.transfer.pending.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static ch.elca.kafka.errorhandling.config.TopicsConfig.PROCESSED_TRANSFER_TOPIC;
import static ch.elca.kafka.errorhandling.transfer.TransferStatus.SETTLED;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalBankTransferSettlementService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    @KafkaListener(groupId = "LocalBankTransferSettlingService", topics = PROCESSED_TRANSFER_TOPIC)
    public void settle(Transfer transfer) {
        Account fromAccount = accountRepository.getByIban(transfer.getIbanFrom());
        BigDecimal newBalance = fromAccount.getBalance().subtract(transfer.getAmountInLocalCurrency());
        fromAccount.setBalance(newBalance);
        accountRepository.save(fromAccount);

        transfer.setStatus(SETTLED);
        transferRepository.save(transfer);
        log.info("Transfer {} settled by local bank.", transfer.getId());
    }
}
