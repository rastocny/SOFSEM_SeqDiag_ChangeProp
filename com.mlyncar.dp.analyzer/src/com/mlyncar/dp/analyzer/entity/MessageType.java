/*
 * Copyright 2017 Andrej Mlyncar <a.mlyncar@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlyncar.dp.analyzer.entity;

import com.mlyncar.dp.analyzer.MessageTypeException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum MessageType {

    SYNCH("synchronous"),
    ASYNCH("asynchronous"),
    CREATE("create"),
    RETURN("return"),
    DESTROY("destroy"),
    SELF("self");

    private final String code;

    private MessageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static MessageType fromCode(String code) throws MessageTypeException {
        for (MessageType type : MessageType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new MessageTypeException("Unknown message type " + code);
    }
}
