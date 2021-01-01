package axon.command.commands;

import axon.command.dto.TransferDTO;
import axon.command.transfer.JejuBankTransferCommand;
import axon.command.transfer.SeoulBankTransferCommand;
import axon.command.transfer.factory.TransferCommandFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;
import java.util.function.Function;

@Builder
@ToString
@Getter
public class MoneyTransferCommand {
    private String srcAccountID;
    @TargetAggregateIdentifier
    private String dstAccountID;
    private Long amount;
    private String transferID;
    private BankType bankType;

    public enum BankType{
        JEJU(command -> new TransferCommandFactory(new JejuBankTransferCommand())),
        SEOUL(command -> new TransferCommandFactory(new SeoulBankTransferCommand()));

        private Function<MoneyTransferCommand, TransferCommandFactory> expression;
        BankType(Function<MoneyTransferCommand, TransferCommandFactory> expression){ this.expression = expression;}
        public TransferCommandFactory getCommandFactory(MoneyTransferCommand command){
            TransferCommandFactory factory = this.expression.apply(command);
            factory.create(command.getSrcAccountID(), command.getDstAccountID(), command.amount, command.getTransferID());
            return factory;
        }
    }

    public static MoneyTransferCommand of(TransferDTO dto){
        return MoneyTransferCommand.builder()
                .srcAccountID(dto.getSrcAccountID())
                .dstAccountID(dto.getDstAccountID())
                .amount(dto.getAmount())
                .bankType(dto.getBankType())
                .transferID(UUID.randomUUID().toString())
                .build();
    }
}
