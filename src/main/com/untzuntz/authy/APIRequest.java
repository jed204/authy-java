package com.untzuntz.authy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.untzuntz.authy.exceptions.APIConnectionException;
import com.untzuntz.authy.exceptions.APIErrorException;
import com.untzuntz.authy.exceptions.APIException;
import com.untzuntz.authy.exceptions.AuthenticationException;
import com.untzuntz.authy.exceptions.AuthyException;

public class APIRequest {

	public static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	protected static final String CHARSET = "UTF-8";
	protected enum Method { GET, POST }

	protected static String urlEncodePair(String k, String v) throws UnsupportedEncodingException {
		return String.format("%s=%s", 
				URLEncoder.encode(k, CHARSET), 
				URLEncoder.encode(v, CHARSET));
	}

	private static HttpURLConnection createConnection(String url) throws IOException {
		URL aURL = new URL(url);
		HttpURLConnection conn = null;
		
		if (Authy.testFlag)
			conn = (HttpURLConnection) aURL.openConnection();
		else
			conn = (HttpsURLConnection) aURL.openConnection(); //use SSL URLs
			
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(90000);
		conn.setUseCaches(false);
		
		return conn;
	}
	
	private static HttpURLConnection createGetConnection(String url, String query) throws IOException {
		String getURL = String.format("%s?%s", url, query);
		HttpURLConnection conn = createConnection(getURL);
		conn.setRequestMethod("GET");
		return conn;
	}

	private static HttpURLConnection createPostConnection(String url, String query) throws IOException {
		HttpURLConnection conn = createConnection(url);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", String.format("application/x-www-form-urlencoded;charset=%s", CHARSET));
		OutputStream output = null;
		try {
		     output = conn.getOutputStream();
		     output.write(query.getBytes(CHARSET));
		} finally {
			if (output != null) output.close();
		}
		return conn;
	}
	
	private static String getResponseBody(InputStream responseStream) throws IOException {
		String rBody = new Scanner(responseStream, CHARSET).useDelimiter("\\A").next(); // \A is the beginning of the stream boundary
		responseStream.close();
		return rBody;
	}

	protected static APIResponse makeURLConnectionRequest(Method method, String url, String query) throws APIConnectionException {
		
		//System.out.println("Type [" + method + "] URL [" + url + "] Query [" + query + "]");
		HttpURLConnection conn = null;
		try {
			switch(method) {
				case GET: conn = createGetConnection(url, query); break;
				case POST: conn = createPostConnection(url, query); break;
				default: throw new APIConnectionException(String.format("Unknown HTTP method %s."));
			}
			int responseCode = conn.getResponseCode(); //triggers the request
			String responseBody = null;
			if (responseCode >= 200 && responseCode < 300) {
				responseBody = getResponseBody(conn.getInputStream());
			} else {
				responseBody = getResponseBody(conn.getErrorStream());
			}
			return new APIResponse(responseCode, responseBody);
			
		} catch (IOException e) {
			throw new APIConnectionException(String.format("Could not connect to Authy (%s). Please check your internet connectivity.", url));
		} finally {
			if (conn != null) { conn.disconnect(); }
		}
	}

	protected static void handleAPIError(String responseBody, int responseCode) throws AuthyException {
		
		//System.out.println(responseCode + " -> Error Response: " + responseBody);
		APIRequest.Error error = APIRequest.gson.fromJson(responseBody, APIRequest.ErrorContainer.class).errors;
		switch(responseCode) {
			case 400: throw new APIErrorException(error); 
			case 401: throw new AuthenticationException(error.message);
			case 503: throw new APIException(error.message);
			default: throw new APIException(error.message);
		}
	}


	protected static class ErrorContainer {
		private Error errors; 
	}

	public static class Error {
		String email;
		String cellphone;
		String token;
		String message;
	}

	protected static class UserContainer {
		protected User user;
	}

	public static class User {
		String id;
	}

	protected static class Token {
		protected String token;
	}

	
}
