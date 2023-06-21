/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.producer;

import static com.common.utils.MessageUtils.mergeHeaders;
import static com.kafkaclient.utils.KafkaHelper.getMessage;
import static com.kafkaclient.utils.KafkaHelper.getRequestReplyHeaders;

import com.blueyonder.service.common.liam.identity.Identity;
import com.common.enums.MessageTopic;
import com.common.message.MessageEntity;
import com.common.message.MessagePayload;
import com.common.message.MessageProducer;
import com.common.message.ReplyMessageProducer;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyMessageFuture;
import org.springframework.stereotype.Service;

/**
 * Kafka message producer.
 */
@Service
public class KafkaMessageProducer<T extends MessageEntity, R extends MessageEntity> implements MessageProducer<T>,
    ReplyMessageProducer<T, R> {

    @Value("${kafka.app.reply.timeout.ms:30000}")
    private int requestReplyTimeout;

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, T, R> requestReplyKafkaTemplate;
    private final Identity identity;

    /**
     * Kafka Message Producer constructor.
     *
     * @param kafkaTemplate             KafkaTemplate.
     * @param requestReplyKafkaTemplate requestReplayKafkaTemplate.
     * @param identity                  identity.
     */
    public KafkaMessageProducer(KafkaTemplate<String, T> kafkaTemplate,
                                ReplyingKafkaTemplate<String, T, R> requestReplyKafkaTemplate, Identity identity) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestReplyKafkaTemplate = requestReplyKafkaTemplate;
        this.identity = identity;
    }

    @Override
    public void send(@NonNull MessageTopic topic, @NonNull T messageEntity, Map<String, Object> headers) {
        kafkaTemplate.send(getMessage(topic, messageEntity, headers, identity));
    }

    @Override
    public MessagePayload<R> sendAndReceive(@NonNull MessageTopic requestTopic, @NonNull T messageEntity,
                                            Map<String, Object> headers)
        throws InterruptedException, ExecutionException, TimeoutException {
        final Map<String, Object> requestReplyHeaders = getRequestReplyHeaders(requestReplyKafkaTemplate);
        final RequestReplyMessageFuture<String, T> replyFuture = requestReplyKafkaTemplate.sendAndReceive(
            getMessage(requestTopic, messageEntity, mergeHeaders(requestReplyHeaders, headers), identity));
        return (MessagePayload<R>) replyFuture.get(requestReplyTimeout, TimeUnit.MILLISECONDS).getPayload();
    }

}
