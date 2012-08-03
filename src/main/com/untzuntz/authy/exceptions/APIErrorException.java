package com.untzuntz.authy.exceptions;

import com.untzuntz.authy.APIRequest;

public class APIErrorException extends AuthyException {

	private static final long serialVersionUID = 1L;
	
	private APIRequest.Error e;
	public APIErrorException(APIRequest.Error e) {
		super("An API error occurred");
	}
	
	public APIRequest.Error getErrorDetails() {
		return e;
	}
	
}
