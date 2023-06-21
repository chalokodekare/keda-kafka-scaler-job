/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.message;

import com.common.utils.IidUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public interface MessageEntity extends Serializable {

    /**
     * Returns topic key.
     *
     * @return
     */
    @JsonIgnore
    default String getKey() {
        return IidUtils.getMessageKey(this);
    }

}
