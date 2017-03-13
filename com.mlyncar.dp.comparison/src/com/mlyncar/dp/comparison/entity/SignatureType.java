package com.mlyncar.dp.comparison.entity;

import com.mlyncar.dp.comparison.exception.SignatureTypeException;

public enum SignatureType {

    EQUAL("equal"),
    DIFFERENT("different"),
    SIMILAR("similar");

    private final String code;

    private SignatureType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ChangeType fromCode(String code) throws SignatureTypeException {
        for (ChangeType changeType : ChangeType.values()) {
            if (changeType.getCode().equals(code)) {
                return changeType;
            }
        }
        throw new SignatureTypeException("Unknown message type " + code);
    }
}
