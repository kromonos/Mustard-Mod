/*
 * MUSTARD: Android's Client for StatusNet
 * 
 * Copyright (C) 2009-2010 macno.org, Michele Azzolari
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.mumod.util;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.mumod.android.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MustardUtil {
	
	public static void snapshot(Context context,String id,String version,String accountNumber) {
		try {
		    HttpPost post = new HttpPost("http://mustard.macno.org/snapshot.php");
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("v", version));
			params.add(new BasicNameValuePair("n", accountNumber));
			params.add(new BasicNameValuePair("m", id));
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpManager hm = new HttpManager(context);
			DefaultHttpClient hc = hm.getHttpClient();
			hc.execute(post);
		} catch(Exception e) {
		}
	}
	
	/** CurrentVersion: the currently installed version of Mustard */
    public static int getCurrentVersion(Context context) {
        return getPrefs(context).getInt(Preferences.CURRENT_VERSION, 0);
    }

    /** CurrentVersion: the currently installed version of Mustard */
    public static void setCurrentVersion(Context context, int version) {
        Editor editor = getPrefs(context).edit();
        editor.putInt(Preferences.CURRENT_VERSION, version);
        editor.commit();
    }
    
    /** Get preferences object from the context */
    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
