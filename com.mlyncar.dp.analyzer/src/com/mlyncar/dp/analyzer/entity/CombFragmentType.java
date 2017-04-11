package com.mlyncar.dp.analyzer.entity;

import com.mlyncar.dp.analyzer.exception.CombFragmentException;

public enum CombFragmentType {

    ALT("alt"),
    OPT("opt"),
    LOOP("loop");

    private final String code;

    private CombFragmentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static CombFragmentType fromCode(String code) throws CombFragmentException {
        for (CombFragmentType type : CombFragmentType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new CombFragmentException("Unknown comb fragment type " + code);
    }
}
