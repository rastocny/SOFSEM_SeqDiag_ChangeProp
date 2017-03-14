package com.mlyncar.dp.synch.exception;

public class ConfigurationException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8668540345712249759L;

	public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
