package com.mlyncar.dp.transformer.entity;

import com.mlyncar.dp.transformer.exception.CombinedFragmentTypeException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum NodeCombinedFragmentType {

    ALT("alt"),
    OPT("opt"),
    LOOP("loop"),
    BREAK("break"),
    PAR("par");

    private final String code;

    private NodeCombinedFragmentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static NodeCombinedFragmentType fromCode(String code) throws CombinedFragmentTypeException {
        for (NodeCombinedFragmentType type : NodeCombinedFragmentType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new CombinedFragmentTypeException("Unknown combined fragment type " + code);
    }
}
