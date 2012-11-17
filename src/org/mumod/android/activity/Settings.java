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

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.android.provider.StatusNet;
import org.mumod.util.ImageManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Settings extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.settings);
		doPrepareButtons();
	}

	private void doPrepareButtons() {
	
		Button mGlobalButton = (Button) findViewById(R.id.btn_global_settings);
		mGlobalButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doStartActivity( GlobalSettings.class);
			}
		});
		
		Button mAccountButton = (Button) findViewById(R.id.btn_account_settings);
		mAccountButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doStartActivity( AccountSettings.class);
			}
		});
		
		Button mOAuthButton = (Button) findViewById(R.id.btn_oauth_settings);
		mOAuthButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doStartActivity( OAuthSettings.class);
			}
		});
		
		Button mFilterButton = (Button) findViewById(R.id.btn_filter_settings);
		mFilterButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doStartActivity( FilterSettings.class);
			}
		});
		
		Button mClearButton = (Button) findViewById(R.id.btn_clear_settings);
		mClearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doClear();
			}
		});
		
	}
	
	private void doStartActivity(Class<?> c) {
		Intent i = new Intent(this, c);
		startActivity(i);
	}
    
	private void doClear() {
		Toast.makeText(this,
				R.string.wait_please,
				Toast.LENGTH_SHORT).show();
		setProgressBarIndeterminateVisibility(true);
		MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
		dbAdapter.open();
		dbAdapter.deleteStatuses(MustardDbAdapter.ROWTYPE_ALL, null);
		MustardApplication _ma = (MustardApplication) getApplication();
		StatusNet statusNet = _ma.checkAccount(dbAdapter);
		dbAdapter.setUserMentionMaxId(statusNet.getUserId(), -1);
		dbAdapter.close();
		ImageManager im = new ImageManager(this);
		im.clear();
		setProgressBarIndeterminateVisibility(false);
		Toast.makeText(this,
				R.string.done,
				Toast.LENGTH_SHORT).show();
	}
}
