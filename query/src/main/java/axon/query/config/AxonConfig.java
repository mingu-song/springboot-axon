package axon.query.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.async.SequentialPerAggregatePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {
    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerTrackingEventProcessor(
                "accounts",
                org.axonframework.config.Configuration::eventStore,
                c -> TrackingEventProcessorConfiguration.forParallelProcessing(3)
                        .andBatchSize(100)
        );

        configurer.registerSequencingPolicy("accounts",
                configuration -> SequentialPerAggregatePolicy.instance());
    }
}
