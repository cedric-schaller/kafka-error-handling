package ch.elca.kafka.errorhandling.transfer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "amountInLocalCurrency", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Transfer toTransfer(TransferDto pendingTransferDto);

    TransferDto toTransferDto(Transfer transfer);
}
