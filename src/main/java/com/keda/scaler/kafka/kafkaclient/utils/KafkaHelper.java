/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.utils;

import static com.common.utils.MessageUtils.getIdentityHeader;
import static com.common.utils.MessageUtils.mergeHeaders;
import static org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import com.blueyonder.service.common.liam.identity.Identity;
import com.common.enums.MessageTopic;
import com.common.message.MessageEntity;
import com.common.message.MessagePayload;
import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
public class KafkaHelper {

    private KafkaHelper() {
        // Private Constructor
    }

    /**
     * Get the assigned partition for request reply kafka template.
     *
     * @param kafkaTemplate request reply kafka template.
     * @return
     */
    private static byte[] getAssignedPartition(ReplyingKafkaTemplate<?, ?, ?> kafkaTemplate) {
        final TopicPartition topicPartition = getFirstAssignedReplyTopicPartition(kafkaTemplate);
        return intToBytesBigEndian(topicPartition.partition());
    }

    private static TopicPartition getFirstAssignedReplyTopicPartition(ReplyingKafkaTemplate<?, ?, ?> kafkaTemplate) {
        final Collection<TopicPartition> assignedPartitions = kafkaTemplate.getAssignedReplyTopicPartitions();
        if (assignedPartitions == null || !assignedPartitions.iterator().hasNext()) {
            throw new KafkaException("No reply partition is assigned to this instance");
        }
        TopicPartition replyPartition = assignedPartitions.iterator().next();
        log.debug("Using partition: {}", replyPartition.partition());
        return replyPartition;
    }

    private static byte[] intToBytesBigEndian(final int data) {
        return new byte[]{(byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff),
            (byte) ((data >> 8) & 0xff), (byte) (data & 0xff),};
    }

    /**
     * Get Request reply headers.
     *
     * @param kafkaTemplate Kafka template.
     * @return
     */
    public static Map<String, Object> getRequestReplyHeaders(ReplyingKafkaTemplate<?, ?, ?> kafkaTemplate) {
        return Map.of(KafkaHeaders.REPLY_PARTITION, com.kafkaclient.utils.KafkaHelper.getAssignedPartition(kafkaTemplate));
    }

    /**
     * Get Kafka Message.
     *
     * @param topic         topic name.
     * @param messageEntity messageEntity.
     * @param headers       headers.
     * @param identity      identity.
     * @param <T>           messageEntity.
     * @return Generic Message.
     */
    public static <T extends MessageEntity> Message<MessagePayload<T>> getMessage(@NonNull MessageTopic topic,
                                                                                  @NonNull T messageEntity,
                                                                                  Map<String, Object> headers,
                                                                                  @NonNull Identity identity) {
        MessagePayload<T> messagePayload = new MessagePayload<>(messageEntity);
        final Map<String, Object> finalHeaders = mergeHeaders(getIdentityHeader(identity), headers);
        return MessageBuilder.withPayload(messagePayload)
            .setHeader(TOPIC, topic.getTopic())
            .setHeader(MESSAGE_KEY, messagePayload.getKey())
            .copyHeadersIfAbsent(finalHeaders)
            .build();
    }

}
