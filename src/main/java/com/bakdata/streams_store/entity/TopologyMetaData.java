package com.bakdata.streams_store.entity;

import lombok.Data;

@Data
public class TopologyMetaData {
    private final String topicName;
    private final String storeName;
}
