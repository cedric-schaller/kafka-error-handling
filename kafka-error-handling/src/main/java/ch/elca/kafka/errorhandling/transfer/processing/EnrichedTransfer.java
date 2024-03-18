package ch.elca.kafka.errorhandling.transfer.processing;

import ch.elca.kafka.errorhandling.exchangerate.ExchangeRate;
import ch.elca.kafka.errorhandling.transfer.Transfer;

public record EnrichedTransfer(Transfer transfer, EnrichedTransfer dltTransfer, ExchangeRate exchangeRate) {
}
