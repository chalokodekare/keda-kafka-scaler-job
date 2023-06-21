/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka;

import static com.common.constants.CommonConstants.EXCEPTION;
import static com.common.enums.MessageTopic.TopicConstants.PRIVATE_SPARQ_ENGINE_STARTUP;

import com.common.aop.AspectLogger;
import com.common.entity.sparq.StartupRequest;
import com.common.message.MessagePayload;
import com.kafkaclient.filter.KafkaRecordFilter;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SparqStartupConsumer {

    private final KafkaTopicRegistryService kafkaTopicRegistryService;
    @Value(KafkaConstants.SPARQ_ENGINE_SIZE)
    private String sparqSize;

    /**
     * Sparq Startup Consumer constructor.
     *
     * @param kafkaTopicRegistryService kafka topic registry service.
     */
    public SparqStartupConsumer(KafkaTopicRegistryService kafkaTopicRegistryService) {
        this.kafkaTopicRegistryService = kafkaTopicRegistryService;
    }

    @PostConstruct
    private void init() {
        log.info("Startup consumer registered for size:{}", sparqSize);
    }

    /**
     * Sparq startup consumer.
     *
     * @param consumerRecord message payload.
     * @param acknowledgment acknowledge.
     */
    @KafkaListener(id = KafkaConstants.SPARQ_ENGINE_SIZE, topics = PRIVATE_SPARQ_ENGINE_STARTUP,
        properties = {KafkaConstants.STARTUP_CONSUMER_PROPS})
    @AspectLogger
    @KafkaRecordFilter
    public void startup(ConsumerRecord<String, MessagePayload<StartupRequest>> consumerRecord,
                        Acknowledgment acknowledgment) {
        try {
            kafkaTopicRegistryService.deRegisterStartupConsumer(sparqSize);
            final StartupRequest startupRequest = consumerRecord.value().getPayload();
            log.info("Request received to startUp sparq engine, instance id: {}", startupRequest.getInstanceId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(EXCEPTION, e);
        }
    }

}
