package axon.command.transfer;

import axon.command.transfer.AbstractCancelTransferCommand;

public class SeoulBankCancelTransferCommand extends AbstractCancelTransferCommand {
    @Override
    public String toString() {
        return "SeoulBankCancelTransferCommand{" +
                "srcAccountID='" + srcAccountID + '\'' +
                ", dstAccountID='" + dstAccountID + '\'' +
                ", amount=" + amount +
                ", transferID='" + transferID + '\'' +
                '}';
    }
}
