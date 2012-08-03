package com.untzuntz.authy.exceptions;

public class AuthyException extends Exception {

	public AuthyException(String message) {
		super(message, null);
	}

	public AuthyException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;

}
