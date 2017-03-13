package com.mlyncar.dp.comparison.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class ChangeTypeException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -613980144619477539L;

    public ChangeTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeTypeException(String message) {
        super(message);
    }
}
