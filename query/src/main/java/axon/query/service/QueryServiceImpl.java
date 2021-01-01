package axon.query.service;

import axon.query.entity.HolderAccountSummary;
import axon.query.loan.LoanLimitQuery;
import axon.query.loan.LoanLimitResult;
import axon.query.query.AccountQuery;
import axon.query.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.Configuration;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryServiceImpl implements QueryService {
    private final Configuration configuration;
    private final AccountRepository repository;
    private final QueryGateway queryGateway;

    @Override
    public void reset() {
        EventProcessingConfiguration eventProcessingConfig = configuration.eventProcessingConfiguration();
        eventProcessingConfig.eventProcessor("accounts", TrackingEventProcessor.class)
                .ifPresent(processor -> {
                    processor.shutDown();
                    processor.resetTokens(repository);
                    processor.start();
                });
    }

    @Override
    public HolderAccountSummary getAccountInfo(String holderId) {
        AccountQuery accountQuery = new AccountQuery(holderId);
        log.debug("handling {}", accountQuery);
        return queryGateway.query(accountQuery, ResponseTypes.instanceOf(HolderAccountSummary.class)).join();
    }

    @Override
    public Flux<HolderAccountSummary> getAccountInfoSubscription(String holderId) {
        AccountQuery accountQuery = new AccountQuery(holderId);
        log.debug("handling {}", accountQuery);

        SubscriptionQueryResult<HolderAccountSummary, HolderAccountSummary> queryResult =
                queryGateway.subscriptionQuery(
                        accountQuery,
                        ResponseTypes.instanceOf(HolderAccountSummary.class),
                        ResponseTypes.instanceOf(HolderAccountSummary.class)
        );

        return Flux.create(emitter -> {
           queryResult.initialResult().subscribe(emitter::next);
           queryResult.updates()
                   .doOnNext(holder -> {
                       log.debug("doOnNext : {}, isCanceled {}", holder, emitter.isCancelled());
                       if (emitter.isCancelled()) {
                           queryResult.close();
                       }
                   })
                   .doOnComplete(emitter::complete)
                   .subscribe(emitter::next);
        });
    }

    @Override
    public List<LoanLimitResult> getAccountInfoScatterGather(String holderId) {
        HolderAccountSummary accountSummary = repository.findByHolderId(holderId).orElseThrow(() -> new NoSuchElementException("소유주가 존재하지 않습니다."));

        return queryGateway.scatterGather(new LoanLimitQuery(accountSummary.getHolderId(), accountSummary.getTotalBalance()),
                ResponseTypes.instanceOf(LoanLimitResult.class),
                30, TimeUnit.SECONDS)
                .collect(Collectors.toList());
    }
}
