package com.mlyncar.dp.transformer.exception;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class GraphTransformationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 5927514380115097957L;

    public GraphTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphTransformationException(String message) {
        super(message);
    }
}
