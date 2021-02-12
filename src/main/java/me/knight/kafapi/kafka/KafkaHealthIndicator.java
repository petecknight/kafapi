package me.knight.kafapi.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    public static final int REQUEST_TIMEOUT_MS = 2000;

    private final AdminClient adminClient;

    private DescribeClusterOptions describeOptions;

    @PostConstruct
    public void setup() {
        this.describeOptions = new DescribeClusterOptions().timeoutMs(REQUEST_TIMEOUT_MS);
    }

    @Override
    public Health health() {
        Builder builder = Health.up();
        try {
            DescribeClusterResult result = this.adminClient.describeCluster(this.describeOptions);
            builder.withDetail("clusterId", result.clusterId().get())
                    .withDetail("brokerId", result.controller().get().idString())
                    .withDetail("nodes", result.nodes().get().size());
        } catch (Exception e) {
            log.warn("Kafka Cluster not ready: {} ({})", e.getClass().getName(), e.getMessage());
            builder = Health.down(e);
        }
        return builder.build();
    }

    @PreDestroy
    public void destroy() {
        this.adminClient.close(30, TimeUnit.SECONDS);
    }

}
