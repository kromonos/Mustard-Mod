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
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class Yourls implements UrlShortener {
	
	protected Context mContext;
	protected SharedPreferences mPreferences;

	public Yourls(Context context) {
		Log.d("Yourls", "Using Yourls own instance");
		mContext=context;
	}	
	
	public String getShorterName() {
		Log.d("Yourls", "getShorterName()");
		return "yourls";
	}
	
	public String doShort(String longUrl, HashMap<String, String> params) throws MustardException {
		return doShort(longUrl, "", "");
	}

	public String doShort(String longUrl, String urlStr, String yourlsAPIKey) throws MustardException {
		if( !urlStr.toLowerCase().startsWith("http") ) {
			urlStr = "http://" + urlStr;
		}
		
		urlStr += "/yourls-api.php";
		
		URL uri;

		try {
			uri = new URL( urlStr );
		} catch (MalformedURLException e) {
			Log.e("Yourls", "Error: " + e.getMessage());
			throw new MustardException(e.getMessage());
		}
		Log.d("Yourls", "URI: " + uri + " - LongURI: " + longUrl);
		HttpManager hm = new HttpManager(mContext,uri.getHost());
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("url", longUrl));
		params.add(new BasicNameValuePair("action", "shorturl"));
		params.add(new BasicNameValuePair("format", "json"));
		
		if ( !yourlsAPIKey.equals("") ) {
			params.add(new BasicNameValuePair("signature", yourlsAPIKey));
		}

		try {
			JSONObject o = null;
			try {
				o = hm.getJsonObject(urlStr, HttpManager.POST, params);	
			}
			catch(Exception e) {
				Toast.makeText(mContext, "Error. Response: " + e.getMessage(), Toast.LENGTH_LONG).show();
				throw new MustardException( e.getMessage() );
			}
			String b1t = o.getString("shorturl");
			String status = o.getString("status");
			String text = o.getString("message");
			Log.d("Yourls", "blt: " + b1t + " - status: " + status + " - text: " + text);
			if( status.equalsIgnoreCase("fail") ) {
				Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
			}

			return b1t;				
		}
		catch (Exception e) {
			Log.e("Yourls", "Yourls error: " + e.getMessage());
			throw new MustardException(e.getMessage());
		}
	
	}
	
}