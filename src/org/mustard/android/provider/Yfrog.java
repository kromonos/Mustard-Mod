package org.mustard.android.provider;

import java.io.File;
import java.util.ArrayList;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mustard.util.HttpManager;

public class Yfrog {

	public static String upload(CommonsHttpOAuthConsumer consumer, String message, File media) throws Exception {
		
		String twauth="https://api.twitter.com/1/account/verify_credentials.json";
		String url = consumer.sign(twauth);
//		String q = url.substring(url.indexOf("?")+1);
//		if (MustardApplication.DEBUG)
//			Log.d("Mustard",q);
//		String[] qs = q.split("&");
//		String xauthserviceprovider="OAuth realm=\"http://api.twitter.com/\"";
//		for (int i=0;i<qs.length;i++) {
//			String[] qsa = qs[i].split("=");
//			xauthserviceprovider +=", "+qsa[0]+"=\""+qsa[1]+"\"";
//		}
//		if (MustardApplication.DEBUG)
//			Log.d("Mustard",xauthserviceprovider);

		HttpManager hm = new HttpManager(null);
		hm.setHost("yfrog.com");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", "macno"));
		params.add(new BasicNameValuePair("verify_url", url));
		params.add(new BasicNameValuePair("auth", "oauth"));
		params.add(new BasicNameValuePair("message",message));
		String ret=hm.getResponseAsString("http://yfrog.com/api/upload", params,"media",media);
		
		return ret;
	}

}
