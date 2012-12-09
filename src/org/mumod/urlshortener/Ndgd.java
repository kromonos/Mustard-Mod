package org.mumod.urlshortener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mumod.util.AuthException;
import org.mumod.util.HttpManager;
import org.mumod.util.MustardException;

import android.content.Context;


public class Ndgd implements UrlShortener {
	
	private String lilUrl ="http://nd.gd";
	
	protected Context mContext;
	
	
	public Ndgd(Context context) {
		mContext=context;
	}

	public String getShorterName() {
		return "nd.gd";
	}

	public String doShort(String longUrl, HashMap<String, String> params)
	throws MustardException {
		return doShort(longUrl, "", "");
	}

	public String doShort(String longUrl, String urlStr, String yourlsAPIKey) throws MustardException {
		URL uri = null;
		try {
			uri =new URL(lilUrl);
		} catch (MalformedURLException e) {
			throw new MustardException(e.getMessage());
		}
		HttpManager hm = new HttpManager(mContext,uri.getHost());
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("longurl", longUrl));
		String ret = "";
		try {

			String html = hm.getResponseAsString(lilUrl,HttpManager.POST,params);

			String pattern = "<p class=\"success\">Your URL is: <a href=\"";
			int pos = html.indexOf(pattern);
			if (pos > 1) {
				int end= html.indexOf("\">",pos+pattern.length());
				ret = html.substring(pos+pattern.length(), end);
			} else {
				throw new MustardException("Unable to find the shorted URL");
			}

			
		} catch (AuthException e) {
			// Never thrown, no auth required
			throw new MustardException("Unauth");
		} catch (IOException e) {
			throw new MustardException(e.getMessage());
		}
		return ret;
	}

}
