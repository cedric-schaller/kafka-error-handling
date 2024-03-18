package ch.elca.kafka.errorhandling.transfer;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
public class Transfer {

    @Id
    String id;
    Instant datetime;
    String ibanFrom;
    String ibanTo;
    BigDecimal amount;
    String currency;
    BigDecimal amountInLocalCurrency;

    @Enumerated(STRING)
    TransferStatus status;
}
