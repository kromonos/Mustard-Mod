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

import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.android.provider.OAuthInstance;
import org.mumod.android.provider.OAuthLoader;
import org.mumod.android.provider.StatusNet;
import org.mumod.oauth.OAuthManager;
import org.mumod.statusnet.StatusNetService;
import org.mumod.util.MustardException;
import org.mumod.util.MustardOAuthException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class OAuthLogin extends Activity {

	private String TAG = getClass().getCanonicalName();
	
	protected static String EXTRA_INSTANCE = "extra.instance";
	
	private EditText mInstanceEdit;
	private CheckBox mForceSSLEdit;
	private Button mOAuthButton;
	private Button mLoginButton;
	
	private String mInstance;
	private MustardDbAdapter mDbHelper; 
	private String mSURL;
	private URL mURL;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauthfirststep);
		//setTitle(getString(R.string.app_name)+" - " + getString(R.string.lbl_create_account));
		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		mInstanceEdit = (EditText) findViewById(R.id.edit_instance);
		mInstanceEdit.setText("");
		mForceSSLEdit = (CheckBox) findViewById(R.id.force_ssl);
		
		mOAuthButton = (Button) findViewById(R.id.button_oauth);
		mOAuthButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doOAuthLogin();
			}
		});

		mLoginButton = (Button) findViewById(R.id.button_login);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLogin();
			}
			
		});

		Intent intent = getIntent();
		String instance = intent.getStringExtra(EXTRA_INSTANCE);
		if (instance != null && !"".equals(instance)) {
			mInstanceEdit.setText(instance);
			// If I have the instance I force the 
			doOAuthLogin();
		}
	}
	
	private void doLogin() {
		BasicAuthLogin.actionHandleLogin(this);
		finish();
	}
	
	public void doOAuthLogin() {
		try {
			// I force a keys download.. 
//			OAuthKeyFetcher oaf = new OAuthKeyFetcher();
//			oaf.execute(this, mDbHelper, null);
			requestToken();
		} catch (MustardOAuthException e) {
			new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(e.getMessage())
            .setNeutralButton(getString(R.string.close), null).show();
		} catch (MalformedURLException e) {
			new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_invalid_url))
            .setNeutralButton(getString(R.string.close), null).show();
		} catch (OAuthNotAuthorizedException e) {
			new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(R.string.oauth_wrong_consumer_key)
            .setNeutralButton(getString(R.string.close), null).show();
		} catch (Exception e) {
			e.printStackTrace();
			new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_generic_detail,e.getMessage()==null ? e.toString() : e.getMessage()))
            .setNeutralButton(getString(R.string.close), null).show();
		}
	}
	
	public void requestToken() throws Exception {
		
		mInstance = mInstanceEdit.getText().toString();
		if (mInstance == null || "".equals(mInstance))
			return;
		mInstance=mInstance.toLowerCase();
		if (mInstance.endsWith("/"))
			mInstance=mInstance.substring(0, mInstance.length()-1);
	
		String instance="";
		if (mInstance.toLowerCase().indexOf("twitter.com")>=0) {
			mForceSSLEdit.setChecked(true);
			mInstance="twitter.com";
			instance=mInstance;
			mSURL = "https://" + mInstance;
			mURL = new URL(mSURL);
			
			doNextStep(instance,true);
		} else {
			if (mInstance.toLowerCase().startsWith("http"))
				mSURL = mInstance;
			else {
				if(mForceSSLEdit.isChecked())
					mSURL = "https://" + mInstance;
				else
					mSURL = "http://" + mInstance;
			}
		
			try {
				mURL = new URL(mSURL);
			} catch (MalformedURLException e) {
				throw e;
			}

			instance = mSURL.startsWith("https") ? mSURL.substring(8) : mSURL.substring(7);

			StatusesNetChecker snc = new StatusesNetChecker(instance);
			snc.execute();
		
		}

	}
	
	private void doNextStep(String instance,boolean isTwitter) {
//	    Log.i(getPackageName(),"mSURL = " + mSURL);
//	    Log.i(getPackageName(),"instance = " + instance);
	   
	    OAuthLoader om = new OAuthLoader(mDbHelper) ;
	    OAuthInstance oi =  om.get(instance);
	    if (oi == null )  {
	    	new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_no_oauth,instance))
            .setNeutralButton(getString(R.string.close), null).show();
			return;
	    }
	    
//        Log.i(getPackageName(),"key: " + oi.key);
//        Log.i(getPackageName(),"key secret: " + oi.secret);
        
	    OAuthManager oauthManager = OAuthManager.getOAuthManager(this);
	    oauthManager.prepare(
	    		oi.key,
	            oi.secret,
        		mSURL + (!isTwitter ? "/api" : "") + "/oauth/request_token",
        		mSURL + (!isTwitter ? "/api" : "") + "/oauth/access_token",
        		mSURL + (!isTwitter ? "/api" : "") + "/oauth/authorize");

	    OAuthExchange oe = new OAuthExchange(oauthManager, instance, isTwitter);
	    oe.execute();
	}
	
	private void doLastStep(String authUrl, OAuthManager oauthManager,String instance, boolean isTwitter) {
        
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String cToken = oauthManager.getConsumer().getToken();
        String cTokenSecret = oauthManager.getConsumer().getTokenSecret();
//        Log.d("Mustard", "Token -> " + cToken);
        mSharedPreferences.edit().putString("Request_token", cToken)
        	.putString("Request_token_secret", cTokenSecret)
        	.putString("oauth_url",mSURL)
        	.putBoolean("is_twitter",isTwitter)
        	.putBoolean("oauth_10a",oauthManager.isOAuth10a())
        	.putString("instance",instance)
        	.commit();
        if(authUrl!=null && !"".equals(authUrl)) {
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
        	setResult(RESULT_OK);
        	finish();
        } else {
        	new AlertDialog.Builder(OAuthLogin.this)
            .setTitle(getString(R.string.error))
            .setMessage("I didn't received the authURL, can't continue.")
            .setNeutralButton(getString(R.string.close), null).show();
			return;
        }
	}
	
	private String isStatusNetInstance() throws MustardException {
		StatusNet mStatusNet = new StatusNet(this);
		mStatusNet.setURL(mURL);
		
		StatusNetService sns = null;
		try {
			// Check if mURL is a statusnet instance
			sns = mStatusNet.getConfiguration();
		} catch (MustardException e) {
//			if(MustardApplication.DEBUG)
//				e.printStackTrace();
			throw new MustardException(getString(R.string.error_help_test));
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(getString(R.string.error_help_test));
		}
		if (sns == null) {
			// this is not a SN instance..
			throw new MustardException(getString(R.string.error_help_test));
		}		
		return "";
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
	
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.account_setup_oauth_option, menu);
        return true;
    }
    
    private void onOAuthSetup() {
    	OAuthSettings.actionOAuthSettings(this);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oauth_settings:
            	onOAuthSetup();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
	public static void actionHandleLogin(Context context,String host) {
		Intent intent = new Intent(context, OAuthLogin.class);
		intent.putExtra(EXTRA_INSTANCE, host);
	    context.startActivity(intent);
	}

	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog;
		switch(id) {
		case MustardBaseActivity.DIALOG_FETCHING_ID:
			// do the work to define the pause Dialog
			dialog = new ProgressDialog(this);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setMessage(getString(R.string.please_wait));
			break;
			
		default:
			if(MustardApplication.DEBUG) Log.d(TAG,"onCreateDialog null....");
			dialog = null;
		}
		return dialog;
	}
	
	public class StatusesNetChecker extends AsyncTask<Void, Integer, Integer> {
		
		private String mInstance;
		
		public StatusesNetChecker(String instance) {
			mInstance=instance;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(MustardBaseActivity.DIALOG_FETCHING_ID);
//			mHandler.progress(true);
		}

		@Override
		protected void onPostExecute(Integer result) {
			try { dismissDialog(MustardBaseActivity.DIALOG_FETCHING_ID); } catch (IllegalArgumentException e) {}
//			mHandler.progress(false);
			if(result>0) {
				doNextStep(mInstance, false);
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				isStatusNetInstance();
			} catch (MustardException e) {
				return -1;
			}
			return 1;
		}
	}
	
	public class OAuthExchange extends AsyncTask<Void, Integer, Integer> {
		
		private String mInstance;
		private boolean mIsTwitter;
		private OAuthManager mOauthManager;
		private String mAuthUrl;
		
		public OAuthExchange(OAuthManager oauthManager, String instance,boolean isTwitter) {
			mInstance=instance;
			mIsTwitter = isTwitter;
			mOauthManager = oauthManager;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(MustardBaseActivity.DIALOG_FETCHING_ID);
//			mHandler.progress(true);
		}

		@Override
		protected void onPostExecute(Integer result) {
//			mHandler.progress(false);
			try { dismissDialog(MustardBaseActivity.DIALOG_FETCHING_ID); } catch (IllegalArgumentException e) {}
			doLastStep(mAuthUrl, mOauthManager, mInstance, mIsTwitter);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			mAuthUrl = mOauthManager.retrieveRequestToken("statusnet://oauth");
			Log.d("Mustard", "retrieveRequestToken: " + mAuthUrl);
			return 1;
		}
	}
}
