package com.mlyncar.dp.transformer.entity;

import com.mlyncar.dp.transformer.exception.MessageTypeException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum EdgeType {

    SYNCH("synchronous"),
    ASYNCH("asynchronous"),
    CREATE("create"),
    RETURN("return"),
    DESTROY("destroy"),
    SELF("self");

    private final String code;

    private EdgeType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static EdgeType fromCode(String code) throws MessageTypeException {
        for (EdgeType type : EdgeType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new MessageTypeException("Unknown message type " + code);
    }
}
