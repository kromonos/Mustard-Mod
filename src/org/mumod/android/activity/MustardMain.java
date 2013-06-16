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

import java.util.Timer;
import java.util.TimerTask;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.Preferences;
import org.mumod.android.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MustardMain extends MustardBaseActivity {

	private SlidingMenu mMenuRight;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
			
		deleteOnExit=false;

		if(mStatusNet!=null) {
			boolean mMergedTimeline = mPreferences.getBoolean(Preferences.CHECK_MERGED_TL_KEY, false);

			String ThemeSet = mPreferences.getString(Preferences.THEME, getString(R.string.theme_bw));
			boolean mLayoutLight = ThemeSet.equals( getString(R.string.theme_bw) );
			mLayoutLight = true;
			if (mLayoutLight) {
				setTheme(android.R.style.Theme_Holo);
			}
			else {
				setTheme(android.R.style.Theme_Holo_Light);
			}
					
			
			doPrepareButtons();
			
			TextView tagInfo = (TextView) findViewById(R.id.dent_info);
			tagInfo.setText(getString(R.string.timeline_main) + (mMergedTimeline ? " (+) " : ""));

			try {
				fillData();
				doSilentRefresh();
			} catch (Exception e) {
				new AlertDialog.Builder(MustardMain.this)
				.setTitle(getString(R.string.error))
				.setMessage(getString(R.string.error_generic_detail,e.getMessage() == null ? e.toString() : e.getMessage()))
				.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface xdialog, int id) {
						finish();
					}
				}).show();
			}
		}
		
		mAutoRefresh = mPreferences.getBoolean(Preferences.AUTO_REFRESH_KEY, false);
		if(mAutoRefresh)
			startTimerTask();
	}

	@Override
	protected void onBeforeFetch() {
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_FRIENDS;
		if (mMergedTimeline)
			DB_ROW_EXTRA="MERGED";
		else
			DB_ROW_EXTRA=mStatusNet.getMUsername();
	}

	@Override
	protected void onAfterFetch() {		
	}

	@SuppressLint("UseValueOf")
	@Override
	protected void onSetListView() {
	     setTitle("Mustard {MOD}");
	        // set the content view
	        setContentView(R.layout.legacy_dents_list);
	        
	        boolean smOrientation = mPreferences.getBoolean(Preferences.SLIDEMENUE, true);
	        boolean fullscreenSwype = mPreferences.getBoolean("settings_fswype", false);
	        Integer smMargin = new Integer(mPreferences.getString("slidemenu_margin", "100"));
	        if( smMargin < 100 ) { smMargin = 100; }
	        if( smMargin > 400 ) { smMargin = 400; }

	        mMenuRight = new SlidingMenu(this);
	        if( smOrientation ) { mMenuRight.setMode(SlidingMenu.RIGHT); }
	        else { mMenuRight.setMode(SlidingMenu.LEFT); }
	        if( fullscreenSwype ) { mMenuRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); }
	        else { mMenuRight.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); }
	        mMenuRight.setBehindOffset(smMargin);
	        mMenuRight.setShadowWidth(15);
	        mMenuRight.setShadowDrawable(R.drawable.shadow);
	        mMenuRight.setFadeDegree(0.50f);
	        mMenuRight.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	        mMenuRight.setMenu(R.layout.settings_slidemenu);
//		setContentView(R.layout.legacy_dents_list);
	}

	public static void actionHandleTimeline(Context context) {
		Intent intent = new Intent(context, MustardMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(intent);
	}

	private Timer t = new Timer();
	

	
	
	private void startTimerTask() {
		
		TimerTask scanTask = new TimerTask() {
			
			@Override
			public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                    	Log.v(TAG, "Timer tick");
                    	doSilentRefresh();
                    }
               });
	        }};

	    String s_delay = mPreferences.getString(Preferences.AUTO_REFRESH_INTERVAL_KEY, getString(R.string.pref_auto_refresh_interval_default));
		long delay = Long.parseLong(s_delay) * 60 * 1000;
	    t.schedule(scanTask, delay, delay); 
	}
	
	
	@Override
	public void onDestroy() {

		super.onDestroy();
		if (MustardApplication.DEBUG) Log.i(TAG,"onDestroy()");

		if (t!=null) {
			t.cancel();
			int i = t.purge();
			if(MustardApplication.DEBUG)
				Log.d(TAG,"Purged " + i + " tasks");
			t = null;
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG,"Id: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.mn_settings:
			doStartSettings();
			break;
		case R.id.mn_bookmarks:
			doBookmark();
			break;
		case R.id.mn_logout:
			doLogout();
			break;
		case R.id.mn_about:
			AboutDialog.show(this);
			break;
		case R.id.mn_compose:
			doCompose();
			break;
		case R.id.mn_mentions:
			getMentions();
			break;
		case R.id.mn_refresh:
			doRefresh();
			break;
		case R.id.mn_search:
			doSearch();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void doPrepareButtons() {
		
		Button mBookmarksButton = (Button) findViewById(R.id.btn_bookmarks);
		mBookmarksButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doBookmark();
			}
		});
		
		Button mRepliesButton = (Button) findViewById(R.id.btn_replies);
		mRepliesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				getMentions();
			}
		});

//		DirectMessageList  btn_directMessages
		Button mDirectMessages = (Button) findViewById(R.id.btn_directMessages);
		mDirectMessages.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( DirectMessageList.class );
			}
		});
		
//		DirectMessageNew 	btn_composeMessage		
		Button mDirectMessageNew = (Button) findViewById(R.id.btn_composeMessage);
		mDirectMessageNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( DirectMessageNew.class );
			}
		});
		
		
		Button mGlobalButton = (Button) findViewById(R.id.btn_global_settings);
		mGlobalButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( GlobalSettings.class );
			}
		});
		
		Button mAccountButton = (Button) findViewById(R.id.btn_account_settings);
		mAccountButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( AccountSettings.class );
			}
		});
		
		Button mOAuthButton = (Button) findViewById(R.id.btn_oauth_settings);
		mOAuthButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( OAuthSettings.class );
			}
		});
		
		Button mFilterButton = (Button) findViewById(R.id.btn_filter_settings);
		mFilterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mMenuRight.toggle();
				doStartActivity( FilterSettings.class );
			}
		});
		
	}

	private void doStartActivity(Class<?> c) {
		Intent i = new Intent(this, c);
		startActivity(i);
	}

}
