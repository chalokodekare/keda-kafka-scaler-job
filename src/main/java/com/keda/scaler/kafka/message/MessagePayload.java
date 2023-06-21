/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.message;

import com.common.message.MessageEntity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload<T extends MessageEntity> implements Serializable {

    private static final long serialVersionUID = -4070229071510437616L;
    private T payload;

    /**
     * Get message key.
     *
     * @return message key.
     */
    public String getKey() {
        if (ObjectUtils.isEmpty(payload)) {
            return null;
        }
        return payload.getKey();
    }

}
