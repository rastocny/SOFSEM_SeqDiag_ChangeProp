package com.mlyncar.dp.interpreter.exception;

public class ResourceStoreException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 552826224125128479L;

	public ResourceStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceStoreException(String message) {
        super(message);
    }
}
