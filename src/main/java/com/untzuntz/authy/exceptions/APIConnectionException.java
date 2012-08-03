package com.untzuntz.authy.exceptions;

public class APIConnectionException extends AuthyException {

	private static final long serialVersionUID = 1L;

	public APIConnectionException(String message) {
		super(message);
	}

	public APIConnectionException(String message, Throwable e) {
		super(message, e);
	}
	
}
