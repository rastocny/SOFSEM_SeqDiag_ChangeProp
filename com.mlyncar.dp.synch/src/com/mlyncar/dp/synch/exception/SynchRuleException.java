package com.mlyncar.dp.synch.exception;

public class SynchRuleException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -4163521089445764925L;

    private final String synchRule;

    public SynchRuleException(String message, Throwable cause, String synchRule) {
        super(message, cause);
        this.synchRule = synchRule;
    }

    public SynchRuleException(String message, String synchRule) {
        super(message);
        this.synchRule = synchRule;
    }

    public String getSynchRule() {
        return this.synchRule;
    }
}
