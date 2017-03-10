package com.mlyncar.dp.comparison.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ChangeLevelException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8200908754278222199L;

	public ChangeLevelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeLevelException(String message) {
        super(message);
    }
}
