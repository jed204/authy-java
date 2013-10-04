# Authy
==========

authy-java - a Java implementation of the Authy API (www.authy.com)

# Maven

Group ID: com.untzuntz.authy
Artifact: authy-java
Version:  1.0.0

# Usage

    import com.untzuntz.authy.Authy

    Authy.apiKey = "your-api-key"


## Registering a user

`Authy.newUser(email, cellPhone, countryCode)` requires the user e-mail address and cellphone. Optionally you can pass in the countryCode or we will asume
USA. The call will return you the authy id for the user that you need to store in your database.

Make the call to get the user id, catch any errors. Upon success, store the authyUserId in your database with your user.

    String authyUserId = Authy.newUser('email@email.com', '555-555-1212', null);
    
## Verifying a user


__NOTE: Token verification is only enforced if the user has completed registration. To change this behaviour see Forcing Verification section below.__  
   
   >*Registration is completed once the user installs and registers the Authy mobile app or logins once successfully using SMS.*

`Authy.verifyToken(token, authyUserId, forceValidation)` takes the authyUserId that you are verifying and the token that you want to verify. You should have the authyUserId in your database. The verification method will throw TokenVerificationException if there is an issue.

    try {
    
      Authy.verifyToken("TOKEN ID PROVIDED BY USER", authyUserId, false);
    
    } catch (TokenVerificationException tve) {
      // the token did not match up, handle appropriately
    }

### Forcing Verification

If you wish to verify tokens even if the user has not yet complete registration, set forceValidation=true when verifying the token.

    Authy.verifyToken("TOKEN ID PROVIDED BY USER", authyUserId, true);

## Requesting a SMS token

This has not been implemented yet.

Copyright
== 

Copyright (c) 2012 John Danner. See LICENSE.txt for
further details.
