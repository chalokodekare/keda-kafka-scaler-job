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
import com.common.message.MessagePayload;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;

/**
 * Abstraction for Message producer.
 *
 * @param <T> message entity.
 */
public interface ReplyMessageProducer<T extends MessageEntity, R extends MessageEntity> {

    /**
     * Send a request reply message.
     *
     * @param requestTopic  requestTopic name.
     * @param messageEntity message Entity.
     * @param headers       message headers.
     */
    MessagePayload<R> sendAndReceive(@NonNull MessageTopic requestTopic, T messageEntity, Map<String, Object> headers)
        throws InterruptedException, ExecutionException, TimeoutException;

}
