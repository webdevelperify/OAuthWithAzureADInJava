package com.oauthjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Set;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.SilentParameters;

public class Authentication {
	
	private final static String CLIENT_ID = "---ENTER CLIENT ID-----";
	private final static String AUTHORITY = "https://login.microsoftonline.com/---ENTER TENANT ID----/";
	private final static String CERTIFICATE_PATH = "-- ENTER THE CERTIFICATE PFX PATH GENERATED THROUGH POWERSHELL SCRIPT----";
	private final static String CERTIFICATE_PASSWORD = "--- PFX PASSWORD DEFINED----";
	private final static Set<String> SCOPE = Collections.singleton("https://---ENTER TENANT NAME (NOT ID)---.sharepoint.com/.default");
	 
 	String line;
	BufferedReader reader;
	StringBuffer responseContent = new StringBuffer();
	
	public String callHttpGetRequest(String urlString, String access_token) throws IOException{
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		con.setRequestProperty("Content-Type", "application/json;odata=verbose");
		con.setRequestProperty("Accept", "application/json;odata=verbose");
		con.setRequestProperty("Authorization", "Bearer " + access_token);
		
		int status = con.getResponseCode();
		
		if(status>299){
			reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}else{
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		}
		responseContent = new StringBuffer();
		while((line = reader.readLine()) != null){
			responseContent.append(line);
		}
		reader.close();
		con.disconnect();
		return responseContent.toString();
	}
	
	public String oauthAuthentication() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException{
		File file = new File(CERTIFICATE_PATH);
		InputStream pkcs12Certificate = new FileInputStream(file); /* Containing PCKS12-formatted certificate*/
		
		IClientCredential credential = ClientCredentialFactory.createFromCertificate(pkcs12Certificate, CERTIFICATE_PASSWORD);
	
		ConfidentialClientApplication cca =
		        ConfidentialClientApplication
		                .builder(CLIENT_ID, credential)
		                .authority(AUTHORITY)
		                .build();
		
		IAuthenticationResult result;
	     try {
	         SilentParameters silentParameters =
	                 SilentParameters
	                         .builder(SCOPE)
	                         .build();

	         // try to acquire token silently. This call will fail since the token cache does not
	         // have a token for the application you are requesting an access token for
	         result = cca.acquireTokenSilently(silentParameters).join();
	     } catch (Exception ex) {
	         if (ex.getCause() instanceof MsalException) {

	             ClientCredentialParameters parameters =
	                     ClientCredentialParameters
	                             .builder(SCOPE)
	                             .build();

	             // Try to acquire a token. If successful, you should see
	             // the token information printed out to console
	             result = cca.acquireToken(parameters).join();
	         } else {
	             // Handle other exceptions accordingly
	             throw ex;
	         }
	     }
	     return result.accessToken();
	}
}
