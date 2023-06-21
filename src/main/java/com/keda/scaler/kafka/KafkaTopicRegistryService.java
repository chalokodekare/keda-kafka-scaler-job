/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka;

import com.common.aop.AspectLogger;
import com.common.entity.sparq.ActionRequest;
import com.common.entity.sparq.StartupRequest;
import com.common.utils.MessageUtils;
import com.kafkaclient.listener.KafkaConsumerListenerFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaTopicRegistryService {

    private final KafkaConsumerListenerFactory<SparqStartupConsumer> startupConsumerListenerFactory;

//    /**
//     * Register all custom listeners.
//     *
//     * @param startupRequest startupRequest.
//     */
//    @AspectLogger
//    public void register(@NonNull StartupRequest startupRequest) {
//        ActionRequest actionRequest = new ActionRequest();
//        actionRequest.setInstanceId(startupRequest.getInstanceId());
//        final String consumerListenerId = MessageUtils.getConsumerListenerId(actionRequest);
//        startupConsumerListenerFactory.register(SparqStartupConsumer.class, consumerListenerId);
//        log.info("Registered custom listeners, instanceId :{} listenerId: {}", startupRequest.getInstanceId(),
//            consumerListenerId);
//    }

    /**
     * De register startup consumer.
     *
     * @param sparqStartupConsumerId startup consumer id.
     */
    @AspectLogger
    public void deRegisterStartupConsumer(@NonNull String sparqStartupConsumerId) {
        startupConsumerListenerFactory.deRegister(sparqStartupConsumerId);
        log.info("De-registered startup consumer topic, consumerId: {}", sparqStartupConsumerId);
    }

}
