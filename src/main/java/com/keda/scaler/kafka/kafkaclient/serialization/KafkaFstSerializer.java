/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.serialization;

import com.common.message.MessageEntity;
import com.common.message.MessagePayload;
import com.common.utils.FstSerializationUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Kafka fst serializer.
 */
@Slf4j
public class KafkaFstSerializer<T extends MessageEntity> implements Serializer<MessagePayload<T>> {

    @Override
    public byte[] serialize(@NonNull String topic, @NonNull MessagePayload data) {
        log.trace("Kafka Fst Serializer invoked for topic: {}", topic);
        return FstSerializationUtils.serialize(data);
    }

}
