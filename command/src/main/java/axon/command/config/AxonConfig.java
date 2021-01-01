package axon.command.config;

import axon.command.aggregate.AccountAggregate;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.Repository;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@AutoConfigureAfter(AxonAutoConfiguration.class)
public class AxonConfig {
//    @Bean
//    public SimpleCommandBus commandBus(TransactionManager transactionManager) {
//        // AxonServerCommandBus 를 통과하지 않고 바로 어플리케이션 안에서 처리
//        return  SimpleCommandBus.builder().transactionManager(transactionManager).build();
//    }

    @Bean
    public AggregateFactory<AccountAggregate> aggregateFactory(){
        return new GenericAggregateFactory<>(AccountAggregate.class);
    }
    @Bean
    public Snapshotter snapshotter(EventStore eventStore, TransactionManager transactionManager){
        return AggregateSnapshotter
                .builder()
                .eventStore(eventStore)
                .aggregateFactories(aggregateFactory())
                .transactionManager(transactionManager)
                .build();
    }
    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(EventStore eventStore, TransactionManager transactionManager){
        final int SNAPSHOT_TRHRESHOLD = 5;
        return new EventCountSnapshotTriggerDefinition(snapshotter(eventStore,transactionManager),SNAPSHOT_TRHRESHOLD);
    }

    @Bean
    public Cache cache(){
        return new WeakReferenceCache();
    }

    @Bean
    public Repository<AccountAggregate> accountAggregateRepository(EventStore eventStore, SnapshotTriggerDefinition snapshotTriggerDefinition, Cache cache){
        return CachingEventSourcingRepository
                .builder(AccountAggregate.class)
                .eventStore(eventStore)
                .snapshotTriggerDefinition(snapshotTriggerDefinition)
                .cache(cache)
                .build();
    }
}
