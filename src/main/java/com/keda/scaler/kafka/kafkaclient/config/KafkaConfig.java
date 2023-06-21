/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.config;

import com.common.enums.MessageTopic;
import com.common.message.MessageEntity;
import com.kafkaclient.interceptor.KafkaRecordInterceptor;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig<T extends MessageEntity, R extends MessageEntity> {

    @Value("${kafka.app.reply.timeout.ms:30000}")
    private int requestReplyTimeout;

    /**
     * Create and configure the replaying kafka template.
     *
     * @param producerFactory producer factory
     * @param factory         factory.
     * @return
     */
    @Bean("requestReplyKafkaTemplate")
    public ReplyingKafkaTemplate<String, T, R> replyingKafkaTemplate(
        ProducerFactory<String, T> producerFactory,
        ConcurrentKafkaListenerContainerFactory<String, R> factory) {
        factory.setRecordInterceptor(new KafkaRecordInterceptor<>());
        ConcurrentMessageListenerContainer<String, R>
            replyContainer = factory.createContainer(MessageTopic.PRIVATE_DEFAULT_REQUEST_REPLY.getTopic());

        final ReplyingKafkaTemplate<String, T, R> replyingKafkaTemplate = new ReplyingKafkaTemplate<>(producerFactory,
            replyContainer);
        replyingKafkaTemplate.setSharedReplyTopic(true);
        replyingKafkaTemplate.setDefaultTopic(MessageTopic.PRIVATE_DEFAULT_REQUEST_REPLY.getTopic());
        replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofMillis(requestReplyTimeout));
        return replyingKafkaTemplate;
    }

    /**
     * Create the kafka template.
     *
     * @param producerFactory producer factory
     * @return
     */
    @Bean("kafkaTemplate")
    public KafkaTemplate<String, T> kafkaTemplate(ProducerFactory<String, T> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
