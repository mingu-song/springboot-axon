package axon.events.transfer;

import axon.command.transfer.factory.TransferCommandFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class MoneyTransferEvent {
    private String dstAccountID;
    private String srcAccountID;
    private Long amount;
    private String transferID;
    private TransferCommandFactory comamndFactory;
}
