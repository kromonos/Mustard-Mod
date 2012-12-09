package org.mumod.urlshortener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mumod.util.HttpManager;
import org.mumod.util.MustardException;

import android.content.Context;

public class B1tit implements UrlShortener {

	protected Context mContext;
	protected String b1tUrl = "http://b1t.it/";
	
	public B1tit(Context context) {
		mContext=context;
	}

	public String doShort(String longUrl, HashMap<String, String> params)
			throws MustardException {
		return doShort(longUrl, "", "");
	}

	public String doShort(String longUrl, String urlStr, String yourlsAPIKey) throws MustardException {
		URL uri = null;
		try {
			uri =new URL(b1tUrl);
		} catch (MalformedURLException e) {
			throw new MustardException(e.getMessage());
		}
		HttpManager hm = new HttpManager(mContext,uri.getHost());
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("url", longUrl));
		try {
			JSONObject o = hm.getJsonObject(b1tUrl, HttpManager.POST,params);
			String b1t = o.getString("id");
			return b1tUrl + b1t;
		} catch (Exception e) {
			throw new MustardException(e.getMessage());
		}
	}

	public String getShorterName() {
		return "b1t.it";
	}
	

}
