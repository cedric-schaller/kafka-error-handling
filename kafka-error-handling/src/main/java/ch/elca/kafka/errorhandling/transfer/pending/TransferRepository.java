package ch.elca.kafka.errorhandling.transfer.pending;

import ch.elca.kafka.errorhandling.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, String> {

}
