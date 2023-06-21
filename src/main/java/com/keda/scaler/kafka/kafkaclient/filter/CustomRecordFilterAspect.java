/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.filter;

import static com.common.utils.CommonUtils.getAnnotation;

import com.common.aop.AspectLogger;
import com.common.message.MessageEntity;
import com.common.message.MessagePayload;
import com.common.utils.BeanFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Common AOP class.
 */
@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class CustomRecordFilterAspect {

    private BeanFactory<Object> beanFactory;

    /**
     * Acknowledge all {@link KafkaRecordFilter} consumer message.
     *
     * @param proceedingJoinPoint joint point.
     * @throws Throwable throwable.
     */
    @Around("@annotation(KafkaRecordFilter)")
    public void kafkaRecordFilter(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature method = (MethodSignature) proceedingJoinPoint.getSignature();
        final KafkaRecordFilter kafkaRecordFilter = getAnnotation(method.getMethod(), KafkaRecordFilter.class);
        boolean doFilter = false;
        final Object[] args = proceedingJoinPoint.getArgs();
        if (!ObjectUtils.isEmpty(args)) {
            log.trace("Kafka record filter, Method args: {}", args);
            doFilter = filterAndAcknowledge(args, kafkaRecordFilter);
        }
        if (!doFilter) {
            proceedingJoinPoint.proceed();
        }
    }

    @AspectLogger
    private boolean filterAndAcknowledge(Object[] args, KafkaRecordFilter kafkaRecordFilter) {
        final String filterBeanName = kafkaRecordFilter.filter();
        final RecordFilterStrategy recordFilterStrategy =
            (RecordFilterStrategy) beanFactory.getBeanByName(filterBeanName);
        if (ObjectUtils.isEmpty(recordFilterStrategy)) {
            log.warn("Record filter strategy bean not found for bean name: {}, skipping record filter", filterBeanName);
            return false;
        }
        Acknowledgment acknowledgment = null;
        ConsumerRecord<String, MessagePayload<MessageEntity>> consumerRecord = null;
        for (Object arg : args) {
            if (arg instanceof ConsumerRecord) {
                consumerRecord = (ConsumerRecord<String, MessagePayload<MessageEntity>>) arg;
            } else if (arg instanceof Acknowledgment) {
                acknowledgment = (Acknowledgment) arg;
            }
        }
        boolean isFilterRecord = recordFilterStrategy.filter(consumerRecord);
        // Acknowledge to all filtered records
        log.trace("Kafka Record filtered:{}, ack:{}", isFilterRecord, consumerRecord);
        if (isFilterRecord && acknowledgment != null) {
            acknowledgment.acknowledge();
            log.trace("Acknowledged the filter message: {}", consumerRecord);
        }
        return isFilterRecord;
    }

}
