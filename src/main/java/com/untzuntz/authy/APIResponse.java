package com.untzuntz.authy;


public class APIResponse {

	int code;
	String body;
	
	public APIResponse(int code, String body) {
		this.code = code;
		this.body = body;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getBody() {
		return body;
	}

}
