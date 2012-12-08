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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;


public class MustardMain extends MustardBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		String ThemeSet = mPreferences.getString(Preferences.THEME, getString(R.string.theme_bw));
//		boolean mLayoutLight = ThemeSet.equals( getString(R.string.theme_bw) );
//		if (mLayoutLight) {
			setTheme(android.R.style.Theme_Holo);
//		}
//		else {
//			setTheme(android.R.style.Theme_Holo_Light);
//		}

		
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		deleteOnExit=false;
		if(mStatusNet!=null) {
			boolean mMergedTimeline = mPreferences.getBoolean(Preferences.CHECK_MERGED_TL_KEY, false);
			
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

	@Override
	protected void onSetListView() {
		setContentView(R.layout.legacy_dents_list);
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
}
