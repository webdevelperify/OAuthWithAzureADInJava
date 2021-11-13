package com.oauthjava;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

public class OAuthJava {

	public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
		
		Authentication auth = new Authentication();
		String access_token = auth.oauthAuthentication();
		System.out.println(access_token);
		String result = auth.callHttpGetRequest("--SHAREPOINT REST API URL---", access_token);
		System.out.println(result);	
	}

}
