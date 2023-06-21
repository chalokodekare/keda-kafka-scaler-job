/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka;

public class KafkaConstants {

    // Kafka
    public static final String SPARQ_ENGINE_SIZE = "${engine.size:T}";
    public static final String STARTUP_CONSUMER_PROPS = "max.poll.records=1";

    private KafkaConstants() {
        // Private constructor
    }

}
