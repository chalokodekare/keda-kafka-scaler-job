/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.filter;

import static com.kafkaclient.constants.KafkaConstants.DEFAULT_KAFKA_RECORD_FILTER_STRATEGY;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaRecordFilter {

    /**
     * Define the filter strategy bean default kafkaRecordFilterStrategy.
     *
     * @return
     */
    String filter() default DEFAULT_KAFKA_RECORD_FILTER_STRATEGY;
}
