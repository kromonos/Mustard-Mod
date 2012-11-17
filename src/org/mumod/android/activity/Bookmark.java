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
import org.mumod.android.provider.StatusNet;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Bookmark extends TabActivity {
	
	private TabHost mTabHost;
	private StatusNet mStatusNet = null;
	private MustardDbAdapter mDbHelper = null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarks);
        

		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
        
		MustardApplication _ma = (MustardApplication) getApplication();
		mStatusNet = _ma.checkAccount(mDbHelper);

		if (mStatusNet == null) {
			finish();
		}
		
		setTitle(getString(R.string.app_name)  + " - " + mStatusNet.getMUsername() + "@" + mStatusNet.getURL().getHost() + " - " + getString(R.string.title_bookmarks));
        mTabHost = getTabHost();
        
        Intent i = new Intent(this,BookmarkList.class);
        i.putExtra(Preferences.BOOKMARK_TYPE, MustardDbAdapter.ROWTYPE_USER);
        i.putExtra(Preferences.USERID, mStatusNet.getUserId());
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_bookmark_users")
        			.setIndicator(getString(R.string.search_users))
        				.setContent(i));
        
        i = new Intent(this,BookmarkList.class);
        i.putExtra(Preferences.BOOKMARK_TYPE, MustardDbAdapter.ROWTYPE_GROUP);
        i.putExtra(Preferences.USERID, mStatusNet.getUserId());
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_bookmark_groups")
        			.setIndicator(getString(R.string.search_groups))
        				.setContent(i));
        i = new Intent(this,BookmarkList.class);
        i.putExtra(Preferences.BOOKMARK_TYPE, MustardDbAdapter.ROWTYPE_TAG);
        i.putExtra(Preferences.USERID, mStatusNet.getUserId());
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_bookmark_tags")
        			.setIndicator(getString(R.string.search_tags))
        				.setContent(i));
        i = new Intent(this,BookmarkList.class);
        i.putExtra(Preferences.BOOKMARK_TYPE, MustardDbAdapter.ROWTYPE_SEARCH);
        i.putExtra(Preferences.USERID, mStatusNet.getUserId());
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_bookmark_searchs")
        			.setIndicator(getString(R.string.search_notices))
        				.setContent(i));
        mTabHost.setCurrentTab(0);
	}
	
	public void onDestroy() {
		if(mDbHelper != null) {
			mDbHelper.close();
		}
		super.onDestroy();
	}
	

}
