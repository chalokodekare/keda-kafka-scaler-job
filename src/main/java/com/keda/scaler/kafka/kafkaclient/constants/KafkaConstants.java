/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.constants;

public class KafkaConstants {

    public static final String DEFAULT_KAFKA_RECORD_FILTER_STRATEGY = "kafkaRecordFilterStrategy";
    public static final int DEFAULT_CUSTOM_LISTENER_CONCURRENCY = 2;
    public static final String DEFAULT_CUSTOM_AUTO_RESET_LATEST = "auto.offset.reset=latest";

    private KafkaConstants() {
        // Private Constructor.
    }
}
