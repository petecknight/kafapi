package me.knight.kafapi.entity;

import lombok.*;

import java.util.List;

@Data
public class ProcessorMetadata {
    private final String host;
    private final int port;
    private final List<Integer> topicPartitions;
}
