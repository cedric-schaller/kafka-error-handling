package ch.elca.kafka.errorhandling.thirdparty.transfer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransferDto {
    private String id;
    private Instant datetime;
    private String ibanFrom;
    private String ibanTo;
    private BigDecimal amount;
    private String currency;
}
