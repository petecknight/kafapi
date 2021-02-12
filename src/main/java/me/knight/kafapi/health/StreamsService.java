package me.knight.kafapi.health;

import me.knight.kafapi.entity.TopologyMetaData;
import me.knight.kafapi.entity.ProcessorMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

@Slf4j
public class StreamsService {

    private final KafkaStreams streams;
    private final String storeName;

    public StreamsService(KafkaStreams streams, TopologyMetaData topologyMetaData) {
        this.streams = streams;
        storeName = topologyMetaData.getStoreName();
    }

    public void start() {
        log.debug("starting streams");
        streams.start();
    }

    public void close() {

        log.debug("stopping streams");

        if (streams != null) {
            try {
                streams.close();
            } catch (Exception ignored) {}
        }
    }

    public List<ProcessorMetadata> processors() {
        return streams.allMetadataForStore(storeName)
                .stream()
                .map(metadata -> new ProcessorMetadata(
                        metadata.host(),
                        metadata.port(),
                        metadata.topicPartitions().stream()
                                .map(TopicPartition::partition)
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    public StreamsMetadata getMetaData(String key) {
        return streams.metadataForKey(storeName, key, Serdes.String().serializer());
    }

    public ReadOnlyKeyValueStore<String, String> getLocalStateStore(String key) {
        return streams.store(storeName, QueryableStoreTypes.keyValueStore());
    }

    public KafkaStreams.State health() {
        return streams.state();
    }

}
