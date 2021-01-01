package axon.command.transfer.factory;

import axon.command.transfer.AbstractTransferCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferCommandFactory {
    private final AbstractTransferCommand transferCommand;

    public void create(String srcAccountID, String dstAccountID, Long amount, String transferID){
        transferCommand.create(srcAccountID, dstAccountID, amount, transferID);
    }

    public AbstractTransferCommand getTransferCommand(){
        return this.transferCommand;
    }
}
