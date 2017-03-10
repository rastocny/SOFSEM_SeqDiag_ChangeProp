package com.mlyncar.dp.comparison.change;

import com.mlyncar.dp.comparison.exception.ChangeTypeException;


/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum ChangeType {
    
    ADD("add"),
    DELETE("delete"),
    MODIFY("modify");

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
