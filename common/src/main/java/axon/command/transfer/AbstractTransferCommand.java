package axon.command.transfer;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public abstract class AbstractTransferCommand {
    @TargetAggregateIdentifier
    protected String srcAccountID;
    protected String dstAccountID;
    protected Long amount;
    protected String transferID;

    public AbstractTransferCommand create(String srcAccountID, String dstAccountID, Long amount, String transferID) {
        this.srcAccountID = srcAccountID;
        this.dstAccountID = dstAccountID;
        this.transferID = transferID;
        this.amount = amount;
        return this;
    }
}
