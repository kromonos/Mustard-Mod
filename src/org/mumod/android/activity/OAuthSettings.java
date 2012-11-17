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
package org.mumod.android.activity;

import java.net.MalformedURLException;
import java.net.URL;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.android.core.OAuthKeyFetcher;
import org.mumod.android.provider.OAuthLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OAuthSettings extends Activity {

	private String TAG = getClass().getCanonicalName();
	
	private Button mRefreshButton;
	private Button mNewButton;
	private EditText mOauthUrlEdit;
	private MustardDbAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauthsettings);
		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		doPrepareButtons();
	}
	
	private void doPrepareButtons() {
		
		mRefreshButton = (Button) findViewById(R.id.btn_oauth_refresh_keys);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				if (MustardApplication.DEBUG) 
					Log.d(TAG,"Refreshing keys");
				try {
					OAuthKeyFetcher okf = new OAuthKeyFetcher();
					okf.execute(getBaseContext(),mDbHelper, null);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});
		
		mNewButton = (Button) findViewById(R.id.btn_oauth_new_keys);
		mNewButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (MustardApplication.DEBUG) Log.d(TAG,"Fetching new keys");
				doFetchNewKeys();
			}
		});
		
		mOauthUrlEdit = (EditText) findViewById(R.id.oauth_new_keys);
		
	}
	
	private void doFetchNewKeys() {
		
		String surl = mOauthUrlEdit.getText().toString();
		if (surl.equals("")) 
			return;
		URL url = null;
		try {
			url = new URL(surl);
		} catch(MalformedURLException e) {
			new AlertDialog.Builder(OAuthSettings.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_invalid_url))
            .setNeutralButton(getString(R.string.close), null).show();
			return;
		}
		int r = 0;
		try {
			OAuthKeyFetcher okf = new OAuthKeyFetcher();
			r = okf.execute(this,mDbHelper, url);
		} catch (Exception e) {
			new AlertDialog.Builder(OAuthSettings.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_generic_detail, e.getMessage() == null ? e.toString() : e.getMessage()))
            .setNeutralButton(getString(R.string.close), null).show();
			return;
		}
		if (r!=OAuthLoader.OK) {
			String msg = "";
			switch(r) {
			case OAuthLoader.KO:
				msg="All keys are invalid";
				break;
			case OAuthLoader.PARTIAL:
				msg="Loaded but some keys are reserved, skipped.";
				break;
			case OAuthLoader.EMPTY:
				msg="Keys are empty";
				break;
			}
			
			new AlertDialog.Builder(OAuthSettings.this)
            .setTitle(getString(R.string.error))
            .setMessage(msg)
            .setNeutralButton(getString(R.string.close), null).show();
			return;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(MustardApplication.DEBUG) Log.i(TAG, "onDestroy");
		if(mDbHelper != null) {
			try {
				mDbHelper.close();
			} catch (Exception e) {
				if (MustardApplication.DEBUG) e.printStackTrace();
			}
		}
		
	}
	
	public static void actionOAuthSettings(Context context) {
		Intent i = new Intent(context, OAuthSettings.class);
		context.startActivity(i);
	}
}
