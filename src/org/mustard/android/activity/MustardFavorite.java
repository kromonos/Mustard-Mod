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

package org.mustard.android.activity;

import org.mustard.android.MustardApplication;
import org.mustard.android.MustardDbAdapter;
import org.mustard.android.R;
import org.mustard.util.MustardException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MustardFavorite extends MustardUserBaseActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		
		if( mStatusNet != null) {
			
			try {
				getStatuses();
				long cUserId=-1;
				try {
					cUserId=Long.parseLong(DB_ROW_EXTRA);
					if (MustardApplication.DEBUG)
						Log.v(TAG,mStatusNet.getUsernameId() + " vs "+ Long.parseLong(DB_ROW_EXTRA) );
				} catch(NumberFormatException e) {
					
				}
				if (mStatusNet.getUsernameId()==cUserId || mStatusNet.getMUsername().equals(DB_ROW_EXTRA)) {
					TextView tagInfo = (TextView) findViewById(R.id.favorites_info);
					tagInfo.setText("Your favorites");
				} else {
					if(mLayoutLegacy)
						setContentView(R.layout.legacy_user_list);
					else
						setContentView(R.layout.user_list);
					mUser = mStatusNet.getUser(DB_ROW_EXTRA);
					prepareUserView();
				}
			} catch (MustardException e) {
				
			}
			
		}
	}

	@Override
	protected void onBeforeFetch() {
		Intent intent = getIntent();
		Uri data = intent.getData();
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_FAVORITES;
		String userid = intent.getStringExtra(EXTRA_USER);
		if(userid == null) {
			DB_ROW_EXTRA=data.getLastPathSegment();
			if(DB_ROW_EXTRA==null) {
				DB_ROW_EXTRA=mStatusNet.getMUsername();
			}
		} else {
			DB_ROW_EXTRA = userid;
		}
	}
	
	@Override
	protected void onSetListView() {
		if(mLayoutLegacy)
			setContentView(R.layout.legacy_favorites_list);
		else
			setContentView(R.layout.favorites_list);
	}
	
	@Override
	protected void onAfterFetch() {		
	}

	public static void actionHandleTimeline(Context context,String userid) {
		Intent intent = new Intent(context, MustardFavorite.class);
		intent.putExtra(EXTRA_USER, userid);
	    context.startActivity(intent);
	}
	
}
