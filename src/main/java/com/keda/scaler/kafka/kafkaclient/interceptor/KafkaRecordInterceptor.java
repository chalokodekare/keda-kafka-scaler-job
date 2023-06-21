/**
 * Copyright © 2022, Blue Yonder, Inc. ALL RIGHTS RESERVED.
 * <p>
 * This software is the confidential information of Blue Yonder, Inc., and is licensed
 * as restricted rights software. The use,reproduction, or disclosure of this software
 * is subject to restrictions set forth in your license agreement with Blue Yonder.
 */
package com.keda.scaler.kafka.kafkaclient.interceptor;

import com.blueyonder.service.common.liam.identity.Identity;
import com.blueyonder.service.common.liam.identity.IdentityScope;
import com.blueyonder.service.common.liam.identity.IdentityUtils;
import com.common.constants.CommonConstants;
import com.common.logger.utils.MdcUtils;
import com.common.message.MessageEntity;
import com.nimbusds.jwt.PlainJWT;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.RecordInterceptor;

public class KafkaRecordInterceptor<T extends MessageEntity> implements RecordInterceptor<String, T> {

    @SneakyThrows
    @Override
    public ConsumerRecord<String, T> intercept(ConsumerRecord<String, T> consumerRecord) {
        final Headers headers = consumerRecord.headers();
        final Header identityHeader = headers.lastHeader(CommonConstants.IDENTITY_CONTEXT_HEADER_NAME);
        final PlainJWT jwt = PlainJWT.parse(new String(identityHeader.value()));
        final Identity identity = IdentityUtils.createIdentity(jwt);
        IdentityScope.setCurrentIdentity(identity);
        MdcUtils.put(identity);
        return consumerRecord;
    }
}
