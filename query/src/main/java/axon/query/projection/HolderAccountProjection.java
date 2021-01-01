package axon.query.projection;

import axon.events.AccountCreationEvent;
import axon.events.DepositMoneyEvent;
import axon.events.HolderCreationEvent;
import axon.events.WithdrawMoneyEvent;
import axon.query.entity.HolderAccountSummary;
import axon.query.query.AccountQuery;
import axon.query.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.*;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.NoSuchElementException;

@Component
@EnableRetry
@AllArgsConstructor
@Slf4j
@AllowReplay
@ProcessingGroup("accounts")
public class HolderAccountProjection {
    private final AccountRepository repository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    protected void on(HolderCreationEvent event, @Timestamp Instant instant) {
        log.debug("projecting {} , timestamp : {}", event, instant.toString());
        HolderAccountSummary accountSummary = HolderAccountSummary.builder()
                .holderId(event.getHolderID())
                .name(event.getHolderName())
                .address(event.getAddress())
                .tel(event.getTel())
                .totalBalance(0L)
                .accountCnt(0L)
                .build();
        repository.save(accountSummary);
    }

    @EventHandler
    @Retryable(value = NoSuchElementException.class)
    protected void on(AccountCreationEvent event, @Timestamp Instant instant){
        log.debug("projecting {} , timestamp : {}", event, instant.toString());
        HolderAccountSummary holderAccount = getHolderAccountSummary(event.getHolderID());
        holderAccount.setAccountCnt(holderAccount.getAccountCnt()+1);
        repository.save(holderAccount);
    }

    @EventHandler
    protected void on(DepositMoneyEvent event, @Timestamp Instant instant){
        log.debug("projecting {} , timestamp : {}", event, instant.toString());
        HolderAccountSummary holderAccount = getHolderAccountSummary(event.getHolderID());
        holderAccount.setTotalBalance(holderAccount.getTotalBalance() + event.getAmount());

        queryUpdateEmitter.emit(AccountQuery.class,
                query -> query.getHolderId().equals(event.getHolderID()),
                holderAccount);

        repository.save(holderAccount);
    }

    @EventHandler
    protected void on(WithdrawMoneyEvent event, @Timestamp Instant instant){
        log.debug("projecting {} , timestamp : {}", event, instant.toString());
        HolderAccountSummary holderAccount = getHolderAccountSummary(event.getHolderID());
        holderAccount.setTotalBalance(holderAccount.getTotalBalance() - event.getAmount());

        queryUpdateEmitter.emit(AccountQuery.class,
                query -> query.getHolderId().equals(event.getHolderID()),
                holderAccount);

        repository.save(holderAccount);
    }

    private HolderAccountSummary getHolderAccountSummary(String holderID) {
        return repository.findByHolderId(holderID)
                .orElseThrow(() -> new NoSuchElementException("소유주가 존재하지 않습니다."));
    }

    @ResetHandler
    private <R> void resetHolderAccountInfo(R resetContext){
        log.debug("######## reset triggered");
        if (resetContext instanceof AccountRepository) {
            ((AccountRepository)resetContext).deleteAll();
        }
    }

    @QueryHandler
    public HolderAccountSummary on(AccountQuery query){
        log.debug("handling {}", query);
        return repository.findByHolderId(query.getHolderId()).orElse(null);
    }
}
