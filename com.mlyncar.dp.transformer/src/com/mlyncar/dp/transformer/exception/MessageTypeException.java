package com.mlyncar.dp.transformer.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class MessageTypeException extends Exception {

    public MessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageTypeException(String message) {
        super(message);
    }
}
