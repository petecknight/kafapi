package com.bakdata.streams_store.service;


import com.bakdata.streams_store.health.StreamsService;
import com.bakdata.streams_store.entity.KeyValueBean;
import com.bakdata.streams_store.entity.ProcessorMetadata;
import com.bakdata.streams_store.entity.TopologyMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContyService {

    private static final String REMOTE_HOST_FORMAT_STRING = "http://%s:%d/%s/%s";
    private static final String RESOURCE_URL = "messages";

    private final KafkaProducer<String, String> kafkaProducer;
    private final TopologyMetaData topologyMetaData;
    private final StreamsService streamsService;
    private final RestTemplate restTemplate;
    private final HostInfo hostInfo;

    @PostConstruct
    public void start() {
        streamsService.start();
    }

    @PreDestroy
    public void clean() {
        streamsService.close();
    }

    public void post(KeyValueBean keyValueBean) {
        kafkaProducer.send(getProducerRecord(keyValueBean), (recordMetadata, e) -> {
            if (e != null) {
                log.error("failed to send message {} exception: {}", keyValueBean, e.getMessage());
            }
        });
    }

    public List<ProcessorMetadata> processors() {
        return streamsService.processors();
    }

    public KeyValueBean valueByKey(String key) {
        final StreamsMetadata streamsMetadata = streamsService.getMetaData(key);
        return streamsMetadata.hostInfo().equals(hostInfo)
                ? fetchFromLocalStateStoreByKey(key)
                : fetchFromRemoteStateStoreByHostAndKey(streamsMetadata.hostInfo(), key);
    }

    private KeyValueBean fetchFromLocalStateStoreByKey(String key) {
        final ReadOnlyKeyValueStore<String, String> localStateStore = streamsService.getLocalStateStore(key);
        Optional<String> optionalResult = Optional.ofNullable(localStateStore.get(key));
        return new KeyValueBean(key, optionalResult.orElseThrow(NotFoundException::new));
    }

    private KeyValueBean fetchFromRemoteStateStoreByHostAndKey(final HostInfo host, String key) {
        return restTemplate.getForObject(buildRemoteHostURL(host, key), KeyValueBean.class);
    }

    private String buildRemoteHostURL(HostInfo host, String key) {
        return format(REMOTE_HOST_FORMAT_STRING, host.host(), host.port(), RESOURCE_URL, key);
    }

    private ProducerRecord<String, String> getProducerRecord(KeyValueBean keyValueBean) {
        return new ProducerRecord<>(topologyMetaData.getTopicName(), keyValueBean.getKey(), keyValueBean.getValue());
    }

}

