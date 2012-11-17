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

import java.net.URL;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.android.provider.OAuthInstance;
import org.mumod.android.provider.OAuthLoader;
import org.mumod.android.provider.StatusNet;
import org.mumod.oauth.OAuthManager;
import org.mumod.statusnet.StatusNetService;
import org.mumod.statusnet.User;
import org.mumod.util.MustardException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OAuthCallback extends Activity {

	private String TAG = getClass().getCanonicalName();

	private String mSURL;
	private MustardDbAdapter mDbHelper;
	private StatusNet mStatusNet;
	private String mUsername;

	//	private CommonsHttpOAuthConsumer mOAuthConsumer; 

	private SharedPreferences mSharedPreferences;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauthcallback);

		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		setButtonClose(true);
		//		continueButton.setEnabled(false);
		doCompleteLogin();
	}
	
	private void doCompleteLogin() {
		
		Uri uri = getIntent().getData();
		if (uri != null && uri.toString().startsWith("statusnet://oauth")) {

			String oauth_problem = uri.getQueryParameter("oauth_problem");
			if (oauth_problem != null && !"".equals(oauth_problem)) {
				String message = "";
			
				if(oauth_problem.equals("user_refused")) {
					message = "You refused the connection.\nCan't proceed";
				} else {
					message= getString(R.string.error_generic_detail,oauth_problem);
				}
				new AlertDialog.Builder(OAuthCallback.this)
				.setTitle(getString(R.string.error))
				.setMessage(message)
				.setNeutralButton(getString(R.string.close), null).show();
				resetSharedProperties(mSharedPreferences);
				return;
			}
			
			String requestTokenSaved = mSharedPreferences.getString("Request_token","");
			String requestToken = uri.getQueryParameter("oauth_token");
			if (requestToken != null && !"".equals(requestToken)) {
				// This is a test...
				// Start test -->
				if (requestTokenSaved.equals("")) {
					Log.e("Mustard", "savedToken: is null but requestToken is set. I try to proceed");
					// <<-- End test
				} else {
					if(!requestToken.equals(requestTokenSaved)) {
//						Log.e("Mustard", "savedToken: " + requestTokenSaved + " != requestToken: " + requestToken);
						new AlertDialog.Builder(OAuthCallback.this)
						.setTitle(getString(R.string.error))
						.setMessage("Token saved and token returned are not the same!\nWhat's up?!?!?")
						.setNeutralButton(getString(R.string.close), null).show();
						resetSharedProperties(mSharedPreferences);
						return;
//					} else {
//						Log.e("Mustard", "savedToken: " + requestTokenSaved + " == requestToken: " + requestToken);
					}
				}
			}

			mSURL=mSharedPreferences.getString("oauth_url","");

			OAuthManager oauthManager = OAuthManager.getOAuthManager(this);

			if(!oauthManager.isReady()) {
				OAuthLoader om = new OAuthLoader(mDbHelper) ;
				String instance = mSharedPreferences.getString("instance","");
				boolean isTwitter = mSharedPreferences.getBoolean("is_twitter", false);
				OAuthInstance oi =  om.get(instance);
				if (oi != null) {

					oauthManager.prepare(
							oi.key,
							oi.secret,
							mSURL + (!isTwitter ? "/api" : "") + "/oauth/request_token",
							mSURL + (!isTwitter ? "/api" : "") + "/oauth/access_token",
							mSURL + (!isTwitter ? "/api" : "") + "/oauth/authorize");
					
					oauthManager.setConsumerTokenWithSecret(requestTokenSaved,
								mSharedPreferences.getString("Request_token_secret",""),mSharedPreferences.getBoolean("oauth_10a", false));

				}
			}
			String verifier = uri.getQueryParameter("oauth_verifier");
			OAuthThread oat = new OAuthThread(oauthManager,verifier);
			oat.execute();
		}
		
	}
	
	private void showError() {
		new AlertDialog.Builder(OAuthCallback.this)
		.setTitle(getString(R.string.error))
		.setMessage(getString(R.string.error_generic))
		.setNeutralButton(getString(R.string.close), null).show();
		resetSharedProperties(mSharedPreferences);
		return;
	}
	
	private void showError(String message) {
		new AlertDialog.Builder(OAuthCallback.this)
		.setTitle(getString(R.string.error))
		.setMessage(message)
		.setNeutralButton(getString(R.string.close), null).show();
		resetSharedProperties(mSharedPreferences);
		return;
	}

	private void setWelcomeMessage() {
		TextView welcomeMessage = (TextView) findViewById(R.id.welcome_label);
		welcomeMessage.setText(getString(R.string.welcome_label,mUsername));
	}

	private void setButtonClose(boolean error) {
		Button continueButton = (Button) findViewById(R.id.button_continue);
		if(error) {
			continueButton.setText(R.string.close);
			
			continueButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						finish();
					} catch (Exception e) {
						e.printStackTrace();
						new AlertDialog.Builder(OAuthCallback.this)
						.setTitle(getString(R.string.error))
						.setMessage(e.toString())
						.setNeutralButton(getString(R.string.close), null).show();
						return;
					}
				}
			});
		} else {
			continueButton.setText(R.string.btn_continue);
			continueButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						startOActivity();
					} catch (Exception e) {
						e.printStackTrace();
						new AlertDialog.Builder(OAuthCallback.this)
						.setTitle(getString(R.string.error))
						.setMessage(e.toString())
						.setNeutralButton(getString(R.string.close), null).show();
						return;
					}
				}
			});
		}
	}
	private void startMainActivity() {
		MustardMain.actionHandleTimeline(this);
		finish();
	}

	private void startOActivity() {
		
		if (mSURL.endsWith("identi.ca") && mStatusNet != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.msg_welcome,mUsername))
			.setTitle(getString(R.string.title_welcome))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					StatusSubscribe ss = new StatusSubscribe(false);
					ss.execute();
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					xdialog.cancel();
					startMainActivity();
				}
			});
			builder.create();
			builder.show();
		} else if  (mSURL.endsWith("twitter.com") && mStatusNet != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.msg_welcome,mUsername))
			.setTitle(getString(R.string.title_welcome))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					StatusSubscribe ss = new StatusSubscribe(true);
					ss.execute();
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					xdialog.cancel();
					startMainActivity();
				}
			});
			builder.create();
			builder.show();
		} else {
			startMainActivity();
		}

	}

	private User verifyUser(OAuthManager oauthManager) throws Exception {
		mStatusNet = new StatusNet(this);
		mStatusNet.setURL(new URL(mSURL));
		mStatusNet.setCredentials(oauthManager.getConsumer(), "");
		return mStatusNet.checkUser();
	}

	private String getVersion(OAuthManager oauthManager,String username) throws Exception {
		mStatusNet = new StatusNet(this);
		mStatusNet.setURL(new URL(mSURL));
		mStatusNet.setCredentials(oauthManager.getConsumer(), username);
		return mStatusNet.getVersion();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(MustardApplication.DEBUG) Log.i(TAG, "onDestroy");
		resetSharedProperties(mSharedPreferences);
		if(mDbHelper != null) {
			try {
				mDbHelper.close();
			} catch (Exception e) {
				if (MustardApplication.DEBUG) e.printStackTrace();
			}
		}

	}

	private void resetSharedProperties(SharedPreferences sharedPreferences) {
		sharedPreferences.edit()
		.remove("Request_token")
		.remove("Request_token_secret")
		.remove("oauth_url")
		.remove("is_twitter")
		.remove("instance")
		.remove("oauth_10a")
		.commit();
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
	
	public class StatusSubscribe extends AsyncTask<Void, Integer, Integer> {

		private boolean mIsTwitter;
		
		public StatusSubscribe(boolean isTwitter) {
			mIsTwitter=isTwitter;
		}
		
		private final String TAG = getClass().getCanonicalName();

		@Override
		protected Integer doInBackground(Void... s) {
			if (MustardApplication.DEBUG) Log.i(TAG, "background task - start");
			try {
				String mustard_uid = mIsTwitter ?  "179569425" : "mustard";
				mStatusNet.doSubscribe(mustard_uid);
			} catch (MustardException e) {
				Log.e("mustard"," Error subscribing.. " + e.getMessage());
			}
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(MustardBaseActivity.DIALOG_FETCHING_ID);
//			mHandler.progress(true);
		}
		
		protected void onPostExecute(Integer result) {
			try { dismissDialog(MustardBaseActivity.DIALOG_FETCHING_ID); } catch (IllegalArgumentException e) {}
			startMainActivity();
		}
	}
	
	public class OAuthThread extends AsyncTask<Void, Integer, Integer> {
		
		
		private OAuthManager mOauthManager;
		private String mVerifier;
		private String mErrorMessage;
		public OAuthThread(OAuthManager oauthManager,String verifier) {
			mOauthManager=oauthManager;
			mVerifier=verifier;
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
			if(result == 0) {
				showError();
			} else if (result == 2) {
				showError(getString(R.string.error_generic_detail,mErrorMessage));
			} else if (result == 3) {
				showError(getString(R.string.error_help_test));
			} else {
				setWelcomeMessage();
				setButtonClose(false);
//				startOActivity();
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			boolean okVer = mOauthManager.retrieveAccessToken(mVerifier);
			if(!okVer) {
				return 0;
			}
			try {

				User u = verifyUser(mOauthManager);
				u.getName();
//				TextView welcomeMessage = (TextView) findViewById(R.id.welcome_label);
//				welcomeMessage.setText(getString(R.string.welcome_label,u.getName()));

				mUsername = u.getScreen_name();
				String consumerToken = mOauthManager.getConsumer().getToken();
				String consumerTokenSecret = mOauthManager.getConsumer().getTokenSecret();
				String version = "";
				if (!mStatusNet.isTwitterInstance())
					version = getVersion(mOauthManager,mUsername);
				else
					version = "0.9.4";
				if (mDbHelper.userExists(mUsername, mSURL) > 0) {
					mDbHelper.updateAccount(mUsername, consumerToken, consumerTokenSecret, mSURL, 1, version);
				} else {
					mDbHelper.createAccount(u.getId(),mUsername,consumerToken, consumerTokenSecret, mSURL, 1, version);
				}
				resetSharedProperties(mSharedPreferences);

				StatusNetService sns = null;
				if(!mStatusNet.isTwitterInstance()) {
					// Check if mURL is a statusnet instance
					sns = mStatusNet.getConfiguration();
//					Log.v("mustard", "############################## textlimit: " + sns.site.textlimit);

				} else {
					sns = new StatusNetService();
					sns.site.textlimit=140;
					sns.site.attachmentMaxSize=10485760; // 10M ?? boh..
				}
				try {
					mDbHelper.setTextlimitInstance(mSURL, sns.site.textlimit);
					mDbHelper.setAttachmentTextLimitInstance(mSURL, sns.site.textlimit, sns.site.attachmentMaxSize);
				} catch(Exception e) {
					Log.e("mustard", e.getMessage());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				mErrorMessage = e.getMessage();
				return 2;
			}
			return 1;
		}
	}
}
