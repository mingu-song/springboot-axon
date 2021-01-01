package axon.command.aggregate;

import axon.command.commands.HolderCreationCommand;
import axon.events.HolderCreationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@RequiredArgsConstructor
@Aggregate
@Slf4j
public class HolderAggregate {
    @AggregateIdentifier
    private String holderID;
    private String holderName;
    private String tel;
    private String address;

    @CommandHandler
    public HolderAggregate(HolderCreationCommand command) {
        log.debug("handling {}", command);
        apply(new HolderCreationEvent(command.getHolderID(), command.getHolderName(), command.getTel(), command.getAddress()));
    }

    @EventSourcingHandler
    protected void createAccount(HolderCreationEvent event) {
        log.debug("applying {}", event);
        this.holderID = event.getHolderID();
        this.holderName = event.getHolderName();
        this.tel = event.getTel();
        this.address = event.getAddress();
    }
}
