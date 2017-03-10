package com.mlyncar.dp.comparison.change;

import com.mlyncar.dp.comparison.exception.ChangeLevelException;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public enum ChangeLevel {

    UNDEFINED("undefined"),
    MINOR("minor"),
    MAJOR("major"),
    CRITICAL("critical");

    private final String code;

    private ChangeLevel(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ChangeType fromCode(String code) throws ChangeLevelException {
        for (ChangeType changeLevel : ChangeType.values()) {
            if (changeLevel.getCode().equals(code)) {
                return changeLevel;
            }
        }
        throw new ChangeLevelException("Unknown message type " + code);
    }
}
