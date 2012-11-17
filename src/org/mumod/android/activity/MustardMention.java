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
import org.mumod.android.Preferences;
import org.mumod.android.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MustardMention extends MustardUserBaseActivity {
	
	public static final String MERGED="merged";
	
	public void onCreate(Bundle savedInstanceState) {
		TAG = "Mustard/Mentions";
		super.onCreate(savedInstanceState);
		mHandler.progress(false);

		isBookmarkEnable=false;
		if( mStatusNet != null) {
			
			if (MustardApplication.DEBUG)
				Log.v(TAG,mStatusNet.getMUsername() + " vs "+ DB_ROW_EXTRA );
			if(mMergedTimeline) {
				TextView tagInfo = (TextView) findViewById(R.id.mention_info);
				tagInfo.setText("Your replies (+)");
			} else 	if (mStatusNet.getMUsername().equals(DB_ROW_EXTRA)) {
				TextView tagInfo = (TextView) findViewById(R.id.mention_info);
				tagInfo.setText("Your replies");
			} else {
				//					if(mLayoutLegacy)
				setContentView(R.layout.legacy_user_list);
				//					else
				//						setContentView(R.layout.user_list);
				//					mUser = mStatusNet.getUser(DB_ROW_EXTRA);
				//					prepareUserView();
				StatusNetUserFetcher _snuf = new StatusNetUserFetcher();
				_snuf.execute();
				return;
			}
			if(mFromService) {
				mForceOnlyBackMenu=true;
				fillData();
			} else {
				getStatuses(mMergedTimeline);
			}

		}
	}

	@Override
	protected void onBeforeFetch() {
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_MENTION;
		
		Intent intent = getIntent();
		if (intent.getExtras().containsKey("FROMSERVICE")) {
			mMergedTimeline = mPreferences.getBoolean(Preferences.CHECK_MERGED_TL_KEY, false);
			mFromService=true;
			isRefreshEnable=false;
			isBookmarkEnable=false;
			if(mMergedTimeline)
				DB_ROW_EXTRA="-1";
		}
		
		
		if (DB_ROW_EXTRA == null && intent.hasExtra(EXTRA_USER))
			DB_ROW_EXTRA=intent.getStringExtra(EXTRA_USER);
		
		if(DB_ROW_EXTRA==null) {
			DB_ROW_EXTRA=mStatusNet.getMUsername();
		} else {
			if (DB_ROW_EXTRA.equals("-1"))
				mMergedTimeline=true;
		}
		
//		if(MustardApplication.DEBUG)
			Log.d(TAG,"Current DB_ROW_EXTRA: " + DB_ROW_EXTRA);
	}
	
	protected void onAfterFetch() {
		if (mUser != null && (mUser.getId() == mStatusNet.getUserId()) ) {
			MustardDbAdapter mDbHelper = getDbAdapter();
			long maxId = mDbHelper.fetchMaxStatusesId(mStatusNet.getUserId(),DB_ROW_TYPE,DB_ROW_EXTRA);
			mDbHelper.setUserMentionMaxId(mStatusNet.getUserId(), maxId);
			mDbHelper.close();
		}
	}

	@Override
	protected void onSetListView() {
//		if(mLayoutLegacy)
			setContentView(R.layout.legacy_mention_list);
//		else
//			setContentView(R.layout.mention_list);
	}

	public static void actionHandleTimeline(Context context,String username) {
		Intent intent = new Intent(context, MustardMention.class);
		intent.putExtra(EXTRA_USER, username);
	    context.startActivity(intent);
	}
	
	public static Intent getActionHandleTimeline(Context context,String username) {
		Intent intent = new Intent(context, MustardMention.class);
		intent.putExtra(EXTRA_USER, username);
	    return intent;
	}
	
	public static Intent getActionHandleTimeline(Context context) {
		Intent intent = new Intent(context, MustardMention.class);
	    return intent;
	}
}
