package com.mlyncar.dp.interpreter.exception;

public class ExecSpecNotFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6394821809250312701L;

	public ExecSpecNotFoundException(String message) {
        super(message);
    }

    public ExecSpecNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
