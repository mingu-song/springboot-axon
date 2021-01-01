package axon.query.service;

import axon.query.entity.HolderAccountSummary;
import axon.query.loan.LoanLimitResult;
import reactor.core.publisher.Flux;

import java.util.List;

public interface QueryService {
    void reset();
    HolderAccountSummary getAccountInfo(String holderId);
    Flux<HolderAccountSummary> getAccountInfoSubscription(String holderId);
    List<LoanLimitResult> getAccountInfoScatterGather(String holderId);
}
