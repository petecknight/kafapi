package me.knight.kafapi.configuration;

import me.knight.kafapi.service.KafkaStateRestorationListener;
import me.knight.kafapi.entity.TopologyMetaData;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaStreamsMetrics;
import lombok.Data;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
@ConfigurationProperties("kafka")
@Data
public class KafkaConfig {

    private String applicationServerHost;
    private int applicationServerPort;
    private String bootstrapServers;
    private String topicName;
    private String applicationId;
    private String storeName;
    private String adminClientId;


    @Bean
    public HostInfo hostInfo(){
        return new HostInfo(applicationServerHost, applicationServerPort);
    }

    @Bean
    public AdminClient adminClient() {

        final Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(AdminClientConfig.CLIENT_ID_CONFIG, adminClientId);
        return AdminClient.create(properties);
    }

    @Bean
    public KafkaProducer kafkaProducer(){

        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
        properties.put(ProducerConfig.ACKS_CONFIG, "all");


        return new KafkaProducer<>(properties,
                Serdes.String().serializer(),
                Serdes.String().serializer());
    }

    @Bean
    public KafkaStreams streams(KafkaStateRestorationListener kafkaStateRestorationListener, MeterRegistry meterRegistry){

        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, String.format("%s:%s", applicationServerHost, applicationServerPort));
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        KeyValueBytesStoreSupplier stateStore = Stores.inMemoryKeyValueStore(storeName);
        final StreamsBuilder builder = new StreamsBuilder();
        builder.table(
                topicName,
                Materialized.<String, String>as(stateStore)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.String())
        );

        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        new KafkaStreamsMetrics(streams).bindTo(meterRegistry);
        streams.setGlobalStateRestoreListener(kafkaStateRestorationListener);
        return streams;
    }

    @Bean
    public TopologyMetaData topologyMetaData() {
        return new TopologyMetaData(topicName, storeName);
    }
}