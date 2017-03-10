package com.mlyncar.dp.transformer.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class MessageTypeException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6217874438931803026L;

    public MessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTypeException(String message) {
        super(message);
    }
}
