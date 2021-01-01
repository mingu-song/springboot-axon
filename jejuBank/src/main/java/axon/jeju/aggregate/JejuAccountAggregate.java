package axon.jeju.aggregate;

import axon.command.transfer.JejuBankTransferCommand;
import axon.events.transfer.TransferApprovedEvent;
import axon.events.transfer.TransferDeniedEvent;
import axon.jeju.command.JejuAccountCreationCommand;
import axon.jeju.event.JejuAccountCreationEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Entity
@Aggregate
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class JejuAccountAggregate {
    @AggregateIdentifier
    @Id
    private String accountID;
    private Long balance;

    @CommandHandler
    public JejuAccountAggregate(JejuAccountCreationCommand command) throws IllegalAccessException {
        log.debug("handling {}", command);
        if (command.getBalance() <= 0)
            throw new IllegalAccessException("유효하지 않은 입력입니다.");
        apply(new JejuAccountCreationEvent(command.getAccountID(), command.getBalance()));
    }

    @EventSourcingHandler
    protected void on(JejuAccountCreationEvent event) {
        log.debug("event {}", event);
        this.accountID = event.getAccountID();
        this.balance = event.getBalance();
    }

    @CommandHandler
    protected void on(JejuBankTransferCommand command) throws InterruptedException {

        log.debug("handling {}", command);
        if (this.balance < command.getAmount()) {
            apply(TransferDeniedEvent.builder()
                    .srcAccountID(command.getSrcAccountID())
                    .dstAccountID(command.getDstAccountID())
                    .amount(command.getAmount())
                    .description("잔고가 부족합니다.")
                    .transferID(command.getTransferID())
                    .build());
        } else {
            apply(TransferApprovedEvent.builder()
                    .srcAccountID(command.getSrcAccountID())
                    .dstAccountID(command.getDstAccountID())
                    .transferID(command.getTransferID())
                    .amount(command.getAmount())
                    .build());
        }
    }

    @EventSourcingHandler
    protected void on(TransferApprovedEvent event) {
        log.debug("event {}", event);
        this.balance -= event.getAmount();
    }
}
