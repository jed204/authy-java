package com.untzuntz.authy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.untzuntz.authy.exceptions.AuthyException;
import com.untzuntz.authy.exceptions.TokenVerificationException;

public class AuthyTest {

	private static String testEmailAddress;
	private static String testCell;
	
	@BeforeClass public static void setUp() {
		Authy.apiKey = "d57d919d11e6b221c9bf6f7c882028f9";
		Authy.testFlag = true;
		
		Random rando = new Random(System.currentTimeMillis());
		StringBuffer un = new StringBuffer();
		for (int i = 0; i < 8; i++)
			un.append( (char)rando.nextInt(26) + 65);
		
		un.append("@authy.com");
		
		testEmailAddress = un.toString();
		testCell = "305-967-" + (rando.nextInt(999) + 1000);
	}
	
	@Test public void testUserCreate() throws AuthyException {
		String authyUserId = Authy.newUser(testEmailAddress, testCell, null);
		assertNotNull(authyUserId);
	}

	@Test public void testTokenValidation() throws AuthyException {
		String authyUserId = Authy.newUser(testEmailAddress, testCell, null);
		assertNotNull(authyUserId);
		
		Authy.verifyToken("0000000", authyUserId, false);
		
		try {
			Authy.verifyToken("XXXXXX", authyUserId, false);
			fail();
		} catch (TokenVerificationException tve) {
			// this should fail
		}
	}

	
}
