/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.listener;

import com.keda.scaler.kafka.MessageTopic;
import com.keda.scaler.kafka.kafkaclient.constants.KafkaConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaCustomListener {

    /**
     * Topic name.
     *
     * @return
     */
    MessageTopic topic();

    /**
     * Auto startup default true.
     *
     * @return
     */
    boolean autoStartup() default true;

    /**
     * Consumer properties.
     *
     * @return
     */
    String[] properties() default {KafkaConstants.DEFAULT_CUSTOM_AUTO_RESET_LATEST};

    /**
     * Consumer concurrency.
     *
     * @return
     */
    int concurrency() default KafkaConstants.DEFAULT_CUSTOM_LISTENER_CONCURRENCY;

    /**
     * Kafka records filter strategy bean name.
     *
     * @return
     */
    String filter() default KafkaConstants.DEFAULT_KAFKA_RECORD_FILTER_STRATEGY;
}
