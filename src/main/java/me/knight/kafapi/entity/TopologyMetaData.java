package me.knight.kafapi.entity;

import lombok.Data;

@Data
public class TopologyMetaData {
    private final String topicName;
    private final String storeName;
}
