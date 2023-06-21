/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka;

import com.common.entity.sparq.EngineStatusMessage;
import com.common.entity.sparq.StartupRequest;
import com.common.entity.subset.SubsetCreateRequest;
import com.common.entity.subset.SubsetStatusMessage;
import com.common.message.MessageEntity;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum MessageTopic {

    PRIVATE_DEFAULT_REQUEST_REPLY(TopicConstants.PRIVATE_DEFAULT_REQUEST_REPLY, MessageEntity.class),
    PRIVATE_SPARQ_ENGINE_STARTUP(TopicConstants.PRIVATE_SPARQ_ENGINE_STARTUP, StartupRequest.class),
    PRIVATE_SPARQ_ENGINE_REQUEST(TopicConstants.PRIVATE_SPARQ_ENGINE_REQUEST, MessageEntity.class),
    PRIVATE_SPARQ_ENGINE_STATUS(TopicConstants.PRIVATE_SPARQ_ENGINE_STATUS, EngineStatusMessage.class),
    PRIVATE_SPARQ_SERVICE_REQUEST(TopicConstants.PRIVATE_SPARQ_SERVICE_REQUEST, StartupRequest.class),
    PRIVATE_SUBSET_CREATE_REQUEST(TopicConstants.PRIVATE_SUBSET_CREATE_REQUEST, SubsetCreateRequest.class),
    PRIVATE_SUBSET_STATUS(TopicConstants.PRIVATE_SUBSET_STATUS, SubsetStatusMessage.class);

    private static final Map<String, MessageTopic> topicNameMap = new HashMap<>();

    static {
        for (MessageTopic messageTopic : MessageTopic.values()) {
            topicNameMap.put(messageTopic.getTopic(), messageTopic);
        }
    }

    private final String topic;
    private final Class<?> payloadType;

    MessageTopic(String topic, Class<?> payloadType) {
        this.topic = topic;
        this.payloadType = payloadType;
    }

    /**
     * Get payload type.
     *
     * @param topic topic name.
     * @return Payload class type.
     */
    public static Class<?> getPayloadType(String topic) {
        if (topicNameMap.containsKey(topic)) {
            return topicNameMap.get(topic).getPayloadType();
        }
        return Object.class;
    }

    public static class TopicConstants {

        public static final String PRIVATE_DEFAULT_REQUEST_REPLY = "private.default.request.reply";
        public static final String PRIVATE_SPARQ_ENGINE_STARTUP = "private.sparqengine.startup";
        public static final String PRIVATE_SPARQ_ENGINE_REQUEST = "private.sparqengine.request";
        public static final String PRIVATE_SPARQ_ENGINE_STATUS = "private.sparqengine.status";
        public static final String PRIVATE_SPARQ_SERVICE_REQUEST = "private.sparqservice.request";
        public static final String PRIVATE_SUBSET_CREATE_REQUEST = "private.subsetservice.createrequest";
        public static final String PRIVATE_SUBSET_STATUS = "private.subsetservice.status";

        private TopicConstants() {
            // Private constructor
        }
    }

}
