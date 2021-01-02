package axon.command.transfer;

import axon.command.transfer.AbstractCancelTransferCommand;

public class JejuBankCancelTransferCommand extends AbstractCancelTransferCommand {
    @Override
    public String toString() {
        return "JejuBankCancelTransferCommand{" +
                "srcAccountID='" + srcAccountID + '\'' +
                ", dstAccountID='" + dstAccountID + '\'' +
                ", amount=" + amount +
                ", transferID='" + transferID + '\'' +
                '}';
    }
}
