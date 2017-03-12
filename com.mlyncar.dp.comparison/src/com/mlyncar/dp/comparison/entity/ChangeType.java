package com.mlyncar.dp.comparison.entity;

import com.mlyncar.dp.comparison.exception.ChangeTypeException;


/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum ChangeType {
    
    LIFELINE_ADD("lifeline_add"),
    MESSAGE_ADD("message_add"),
    LIFELINE_REMOVE("lifeline_remove"),
    MESSAGE_REMOVE("message_remove"),
    LIFELINE_MODIFY("lifeline_modify"),
    MESSAGE_MODIFY("message_modify"),
    FRAGMENT_ADD("fragment_add"),
    FRAGMENT_REMOVE("fragment_remove"),
    FRAGMENT_MODIFY("fragment_modify");

    private final String code;

    private ChangeType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ChangeType fromCode(String code) throws ChangeTypeException {
        for (ChangeType changeType : ChangeType.values()) {
            if (changeType.getCode().equals(code)) {
                return changeType;
            }
        }
        throw new ChangeTypeException("Unknown message type " + code);
    }
}
