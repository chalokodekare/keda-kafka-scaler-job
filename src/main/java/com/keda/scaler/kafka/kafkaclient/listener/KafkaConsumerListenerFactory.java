/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.listener;

import static com.common.enums.Component.COMMON;
import static com.common.exception.ErrorCode.NOT_FOUND;
import static com.common.utils.CommonUtils.getDefaultIfNull;

import com.common.aop.AspectLogger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerListenerFactory<T> {

    private final KafkaListenerEndpointRegistry listenerEndpointRegistry;
    private final KafkaListenerContainerFactory<?> listenerContainerFactory;
    private final MethodKafkaListenerFactory<T> methodKafkaListenerFactory;

    /**
     * Kafka Consumer Listener Factory constructor.
     *
     * @param listenerEndpointRegistry   kafka listener endpoint registry.
     * @param listenerContainerFactory   kafka listener container factory.
     * @param methodKafkaListenerFactory method kafka listener factory.
     */
    public KafkaConsumerListenerFactory(KafkaListenerEndpointRegistry listenerEndpointRegistry,
                                        KafkaListenerContainerFactory<?> listenerContainerFactory,
                                        MethodKafkaListenerFactory<T> methodKafkaListenerFactory) {
        this.listenerEndpointRegistry = listenerEndpointRegistry;
        this.listenerContainerFactory = listenerContainerFactory;
        this.methodKafkaListenerFactory = methodKafkaListenerFactory;
    }

    /**
     * Register consumer listener.
     *
     * @param clazz              class type.
     * @param consumerListenerId consumer Listener Id.
     */
    @AspectLogger
    public void register(@NonNull Class<T> clazz, @NonNull String consumerListenerId) {
        MethodKafkaListenerEndpoint<String, ?> kafkaListenerEndpoint = methodKafkaListenerFactory.create(clazz,
            consumerListenerId);
        log.info("Registering the kafka consumer listener with autoStartup: {}, (consumerId: {}) for topic: {} ",
            kafkaListenerEndpoint.getAutoStartup(), kafkaListenerEndpoint.getId(), kafkaListenerEndpoint.getTopics());

        listenerEndpointRegistry.registerListenerContainer(kafkaListenerEndpoint, listenerContainerFactory,
            getDefaultIfNull(kafkaListenerEndpoint.getAutoStartup(), true));
    }

    /**
     * Deregister consumer listener by id.
     *
     * @param id consumer id.
     */
    @AspectLogger
    public void deRegister(@NonNull String id) {
        final MessageListenerContainer listenerContainer = listenerEndpointRegistry.getListenerContainer(id);
        if (listenerContainer == null) {
            throw new Exception(COMMON, NOT_FOUND, null, id);
        }
        listenerContainer.stop();
        log.info("Stopped the lister container for id:{}", id);
    }

}
