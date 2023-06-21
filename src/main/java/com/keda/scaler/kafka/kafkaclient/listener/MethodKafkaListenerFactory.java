/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.listener;

import static com.common.constants.CommonConstants.EXCEPTION;
import static com.common.enums.Component.COMMON;
import static com.common.exception.ErrorCode.NOT_FOUND;
import static com.common.utils.CommonUtils.getAnnotation;
import static com.common.utils.CommonUtils.getMethod;

import com.common.aop.AspectLogger;
import com.common.utils.BeanFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class MethodKafkaListenerFactory<T> {

    private final BeanFactory<Object> beanFactory;

    /**
     * Create method kafka listener endpoint.
     *
     * @param clazz              class type.
     * @param consumerListenerId consumer listener id.
     * @return
     */
    @AspectLogger
    public MethodKafkaListenerEndpoint create(Class<T> clazz, String consumerListenerId) {

        MethodKafkaListenerEndpoint kafkaListenerEndpoint = new MethodKafkaListenerEndpoint();
        final Method method = getMethod(clazz, KafkaCustomListener.class);
        if (method == null) {
            throw new Exception(COMMON, NOT_FOUND, null, null, KafkaCustomListener.class);
        }
        final KafkaCustomListener kafkaCustomListener = getAnnotation(method, KafkaCustomListener.class);
        kafkaListenerEndpoint.setId(consumerListenerId);
        kafkaListenerEndpoint.setGroupId(consumerListenerId);
        kafkaListenerEndpoint.setAutoStartup(kafkaCustomListener.autoStartup());
        kafkaListenerEndpoint.setTopics(kafkaCustomListener.topic().getTopic());
        kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
        kafkaListenerEndpoint.setBean(getConsumerBean(clazz));
        kafkaListenerEndpoint.setConcurrency(kafkaCustomListener.concurrency());
        kafkaListenerEndpoint.setConsumerProperties(getProperties(kafkaCustomListener.properties()));
        final RecordFilterStrategy recordFilterStrategyBean =
            (RecordFilterStrategy) beanFactory.getBeanByName(kafkaCustomListener.filter());
        if (recordFilterStrategyBean != null) {
            kafkaListenerEndpoint.setRecordFilterStrategy(recordFilterStrategyBean);
            kafkaListenerEndpoint.setAckDiscarded(true);
        }
        kafkaListenerEndpoint.setMethod(method);
        return kafkaListenerEndpoint;
    }

    @AspectLogger
    private Properties getProperties(String[] stringProperties) {
        Properties properties = new Properties();
        if (!ObjectUtils.isEmpty(stringProperties)) {
            Arrays.stream(stringProperties).forEach(property -> loadProperties(properties, property));
        }
        return properties;
    }

    private void loadProperties(Properties properties, String property) {
        try {
            properties.load(new StringReader(property));
        } catch (IOException e) {
            log.error(EXCEPTION, e);
        }
    }

    @AspectLogger
    private T getConsumerBean(Class<T> consumerClass) {
        return (T) beanFactory.getBeanByClass(consumerClass);
    }

}
