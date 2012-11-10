package org.mustard.oauth;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.client.HttpClient;
import org.mustard.util.HttpManager;

import android.content.Context;
import android.util.Log;

public class OAuthManager {

	private static OAuthManager sOAuthManager;
	
	private CommonsHttpOAuthConsumer mConsumer;
	private CommonsHttpOAuthProvider mProvider;
	private HttpClient mClient;
	
	private OAuthManager(Context context) {
	    HttpManager hm = new HttpManager(context);
	    mClient = hm.getHttpClient();
	}
	
	public static synchronized OAuthManager getOAuthManager(Context context) {
		if (sOAuthManager == null) {
//			Log.i("Mustard", "OAuthManager new instance created");
//			System.setProperty("debug", "aaa");
			sOAuthManager = new OAuthManager(context);
//		} else {
//			Log.i("Mustard", "OAuthManager existing instance reused");
		}
		return sOAuthManager;
	}
		
	public void prepare(String key, String secret, String requestTokenUrl, String accessTokenUrl, String authorizeUrl) {
	    
		mConsumer =  new CommonsHttpOAuthConsumer (
		    		key,
		            secret);
		mProvider = new CommonsHttpOAuthProvider(
				requestTokenUrl,
				accessTokenUrl,
				authorizeUrl,
				mClient);
	}
	
	public void setConsumerTokenWithSecret(String token, String secret,boolean isOauth10a) {
		mConsumer.setTokenWithSecret(token, secret);
		mProvider.setOAuth10a(isOauth10a);
	}
	
	public boolean isReady() {
		return !(mProvider == null || mConsumer == null);
	}
	
	public String retrieveRequestToken(String callbackUrl) {
		if (mProvider == null || mConsumer == null) {
			Log.e("Mustard", "retrivedRequestToken called *before* prepare!");
			return null;
		}
		try {
			return mProvider.retrieveRequestToken(mConsumer,callbackUrl);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean retrieveAccessToken(String verifier) {
		if (mProvider == null || mConsumer == null) {
			Log.e("Mustard", "retrieveAccessToken called *before* prepare!");
			return false;
		}
		try {
			mProvider.retrieveAccessToken(mConsumer,verifier);
			return true;
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return false;
	}
    
    public synchronized static void complete() {
    	sOAuthManager = null;
    }
    
    public CommonsHttpOAuthConsumer getConsumer() {
    	if (mConsumer == null) {
			Log.e("Mustard", "getConsumer called *before* prepare!");
			return null;
		}
    	return mConsumer;
    }
    
    public boolean isOAuth10a() {
    	if(mProvider==null)
    		return false;
    	return mProvider.isOAuth10a();
    }
}
