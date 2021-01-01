package axon.command.saga;

import axon.command.commands.TransferApprovedCommand;
import axon.command.transfer.factory.TransferCommandFactory;
import axon.events.DepositCompletedEvent;
import axon.events.transfer.MoneyTransferEvent;
import axon.events.transfer.TransferApprovedEvent;
import axon.events.transfer.TransferDeniedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class TransferManager {
    @Autowired
    private transient CommandGateway commandGateway;
    private TransferCommandFactory commandFactory;

    @StartSaga
    @SagaEventHandler(associationProperty = "transferID")
    protected void on(MoneyTransferEvent event) {
        log.debug("Created saga instance");
        log.debug("event : {}", event);
        commandFactory = event.getComamndFactory();
        SagaLifecycle.associateWith("srcAccountID", event.getSrcAccountID());

        log.info("계좌 이체 시작 : {} ", event);
        commandGateway.send(commandFactory.getTransferCommand());
    }

    @SagaEventHandler(associationProperty = "srcAccountID")
    protected void on(TransferApprovedEvent event) {
        log.info("이체 금액 {} 계좌 반영 요청 : {}", event.getAmount(), event);
        SagaLifecycle.associateWith("accountID", event.getDstAccountID());
        commandGateway.send(TransferApprovedCommand.builder()
                .accountID(event.getDstAccountID())
                .amount(event.getAmount())
                .transferID(event.getTransferID())
                .build());
    }

    @SagaEventHandler(associationProperty = "srcAccountID")
    protected void on(TransferDeniedEvent event) {
        log.info("계좌 이체 실패 : {}", event);
        log.info("실패 사유 : {}", event.getDescription());
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "accountID")
    @EndSaga
    protected void on(DepositCompletedEvent event){
        log.info("계좌 이체 성공 : {}", event);
    }
}
