package ch.elca.kafka.errorhandling.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {

    @Id
    long id;
    String iban;
    BigDecimal balance;
}
