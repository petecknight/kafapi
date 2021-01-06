package com.bakdata.streams_store.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContyStateRestorationListener implements StateRestoreListener {

    @Override
    public void onRestoreStart(final TopicPartition topicPartition,
                               final String storeName,
                               final long startingOffset,
                               final long endingOffset) {

        log.info("Started restoration of {} partition {} ", storeName, topicPartition.partition());
        log.info(" total records to be restored {}", (endingOffset - startingOffset));
    }

    @Override
    public void onBatchRestored(final TopicPartition topicPartition,
                                final String storeName,
                                final long batchEndOffset,
                                final long numRestored) {

        log.info("Restored batch {} for {} partition {}", numRestored, storeName, topicPartition.partition());

    }

    @Override
    public void onRestoreEnd(final TopicPartition topicPartition,
                             final String storeName,
                             final long totalRestored) {

        log.info("Restoration complete for {} partition {}", storeName, topicPartition.partition());
    }
}