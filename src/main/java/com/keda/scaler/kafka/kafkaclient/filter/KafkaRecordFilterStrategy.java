/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.filter;

import static com.common.constants.CommonConstants.EXCEPTION;

import com.common.aop.AspectLogger;
import com.common.message.MessageEntity;
import com.common.message.MessagePayload;
import com.common.utils.MessageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

@Aspect
@Component("kafkaRecordFilterStrategy")
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AllArgsConstructor
public class KafkaRecordFilterStrategy implements RecordFilterStrategy<String, MessagePayload<MessageEntity>> {

    private final KafkaListenerEndpointRegistry listenerEndpointRegistry;

    @Override
    @AspectLogger
    public boolean filter(ConsumerRecord<String, MessagePayload<MessageEntity>> consumerRecord) {
        if (ObjectUtils.isEmpty(consumerRecord) || ObjectUtils.isEmpty(consumerRecord.value())) {
            log.info("Consumer record is empty, applying record filter");
            return true;
        }
        final String consumerId = getConsumerId(consumerRecord.value().getPayload());
        log.debug("Filtering non instance message, consumerId: {}", consumerId);
        if (ObjectUtils.isEmpty(consumerId)) {
            return true;
        }
        MessageListenerContainer listenerContainer = listenerEndpointRegistry.getListenerContainer(consumerId);
        log.trace("ConsumerId: {} instance record isFiltered: {}", consumerId, listenerContainer == null);
        return listenerContainer == null;
    }

    @AspectLogger
    private String getConsumerId(MessageEntity messageEntity) {
        try {
            return MessageUtils.getConsumerListenerId(messageEntity);
        } catch (Exception e) {
            log.error(EXCEPTION, e);
        }
        return null;
    }

    @Override
    @AspectLogger
    public List<ConsumerRecord<String, MessagePayload<MessageEntity>>> filterBatch(
        List<ConsumerRecord<String, MessagePayload<MessageEntity>>> consumerRecords) {
        log.debug("Batch filtering message");
        List<ConsumerRecord<String, MessagePayload<MessageEntity>>> filteredBatches = new ArrayList<>();
        if (!ObjectUtils.isEmpty(consumerRecords)) {
            filteredBatches = consumerRecords.stream()
                .filter(ObjectUtils::isNotEmpty)
                .filter(consumerRecord -> !filter(consumerRecord))
                .collect(Collectors.toList());
        }
        return filteredBatches;
    }

}
