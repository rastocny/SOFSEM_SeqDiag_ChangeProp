package com.mlyncar.dp.analyzer.exception;

public class AnalyzerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3682640617471292077L;

	public AnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalyzerException(String message) {
        super(message);
    }
}
