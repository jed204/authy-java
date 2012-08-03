package com.untzuntz.authy;

import com.untzuntz.authy.APIRequest.Method;
import com.untzuntz.authy.exceptions.APIException;
import com.untzuntz.authy.exceptions.AuthenticationException;
import com.untzuntz.authy.exceptions.AuthyException;
import com.untzuntz.authy.exceptions.BadRequestException;
import com.untzuntz.authy.exceptions.TokenVerificationException;

/**
 * An Java implements of the Authy API for two-factor authentication
 * 
 * @author jdanner
 *
 */
public class Authy {

	public static final String API_BASE = "https://api.authy.com/protected/json";
	public static final String API_SANDBOX_BASE = "http://sandbox-api.authy.com/protected/json";
	public static final String CLIENT_VERSION = "1.0";
	public static String apiKey;
	public static boolean testFlag;

	/**
	 * Creates a new user @ Authy
	 * @param email user's email address
	 * @param cellPhone user's cell phone
	 * @param countryCode user's cell phone country code (optional, defaults to 1 for USA)
	 * @return Authy user id - keep this in your database for later token requests
	 * @throws AuthyException
	 */
	public static String newUser(String email, String cellPhone, String countryCode) throws AuthyException
	{
		if (apiKey == null || apiKey.length() == 0)
		{
			throw new AuthenticationException("No API key provided. Set Authy.apiKey on app startup.");
		}
		
		if (email == null || email.length() == 0 || cellPhone == null || cellPhone.length() == 0)
			throw new APIException("Required parameters are missing [email | cellphone are required]");
		
		StringBuffer url = new StringBuffer();
		if (testFlag)
			url.append(API_SANDBOX_BASE);
		else
			url.append(API_BASE);
		
		url.append("/users/new?api_key=").append(apiKey);
		
		StringBuffer query = new StringBuffer();
		try {
			query.append(APIRequest.urlEncodePair("user[email]", email)).append("&");
			query.append(APIRequest.urlEncodePair("user[cellphone]", cellPhone));
			
			if (countryCode != null)
				query.append("&").append(APIRequest.urlEncodePair("user[country_code]", countryCode));
		} catch (Exception e) {
			throw new BadRequestException("Unable to encode parameters to " + APIRequest.CHARSET);
		}
		
		APIResponse resp = APIRequest.makeURLConnectionRequest(Method.POST, url.toString(), query.toString());
		
		int responseCode = resp.getCode();
		String responseBody = resp.getBody();
		if (responseCode < 200 || responseCode >= 300) {
			APIRequest.handleAPIError(responseBody, responseCode);
		}

		//System.out.println("Body: " + responseBody);
		
		APIRequest.User user = APIRequest.gson.fromJson(responseBody, APIRequest.UserContainer.class).user;
		return user.id;
	}
	
	/**
	 * Verifies a token for a user
	 * @param token
	 * @param authyUserId
	 * @param forceValidation
	 * @throws AuthyException, TokenVerificationException
	 */
	public static void verifyToken(String token, String authyUserId, boolean forceValidation) throws TokenVerificationException,AuthyException
	{
		if (apiKey == null || apiKey.length() == 0)
		{
			throw new AuthenticationException("No API key provided. Set Authy.apiKey on app startup.");
		}
		
		if (token == null || token.length() == 0 || authyUserId == null || authyUserId.length() == 0)
			throw new APIException("Required parameters are missing [token | authyUserId are required]");
		
		StringBuffer url = new StringBuffer();
		if (testFlag)
			url.append(API_SANDBOX_BASE);
		else
			url.append(API_BASE);

		url.append("/verify/").append(token).append("/").append(authyUserId);
	
		StringBuffer query = new StringBuffer();
		try {
			query.append(APIRequest.urlEncodePair("api_key", apiKey));
			
			if (forceValidation)
				query.append(APIRequest.urlEncodePair("force", "true"));
		} catch (Exception e) {
			throw new BadRequestException("Unable to encode parameters to " + APIRequest.CHARSET);
		}
			
		APIResponse resp = APIRequest.makeURLConnectionRequest(APIRequest.Method.GET, url.toString(), query.toString());
		
		int responseCode = resp.getCode();
		String responseBody = resp.getBody();
		
		if (responseCode == 401)
			throw new TokenVerificationException();
		
		if (responseCode < 200 || responseCode >= 300) {
			APIRequest.handleAPIError(responseBody, responseCode);
		}

		APIRequest.Token tokenResp = APIRequest.gson.fromJson(responseBody, APIRequest.Token.class);
		if (!"is valid".equalsIgnoreCase(tokenResp.token))
			throw new TokenVerificationException();
	}
	
}
