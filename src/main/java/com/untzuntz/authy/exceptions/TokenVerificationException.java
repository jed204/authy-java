package com.untzuntz.authy.exceptions;

public class TokenVerificationException extends AuthyException {

	private static final long serialVersionUID = 1L;

	public TokenVerificationException() {
		super("Token is invalid");
	}
	
}
