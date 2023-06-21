/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.message;

import com.common.enums.MessageTopic;
import com.common.message.MessageEntity;
import java.util.Map;
import lombok.NonNull;

/**
 * Abstraction for Message producer.
 *
 * @param <T> message entity.
 */
public interface MessageProducer<T extends MessageEntity> {

    /**
     * Send asynchronised message.
     *
     * @param topic         topic name.
     * @param messageEntity message Entity.
     * @param headers       message headers.
     */
    void send(@NonNull MessageTopic topic, T messageEntity, Map<String, Object> headers);

}
