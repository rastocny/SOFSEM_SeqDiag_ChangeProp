package com.mlyncar.dp.transformer.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class CombinedFragmentTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2873581110114255898L;

	public CombinedFragmentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CombinedFragmentTypeException(String message) {
        super(message);
    }
}
