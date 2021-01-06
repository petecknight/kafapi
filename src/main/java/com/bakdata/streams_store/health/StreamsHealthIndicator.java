package com.bakdata.streams_store.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamsHealthIndicator implements HealthIndicator {

    private final StreamsService streamsService;

    @Override
    public Health health() {
        KafkaStreams.State state = streamsService.health();

        switch (state) {
            case RUNNING:
            case REBALANCING: {
                return Health.up().build();
            }
            default: {
                log.warn("Stream builder is [{}]", state);
                return Health.down().withDetail("state", state).build();
            }
        }
    }
}
