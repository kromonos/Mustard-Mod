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

import org.mumod.android.Preferences;
import org.mumod.android.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;

public class DirectMessageTab extends TabActivity {
	
	private TabHost mTabHost;

	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.directmessages);
		
		setTitle(getString(R.string.app_name)  + " " + getString(R.string.title_directmessages));
        mTabHost = getTabHost();
        
        Intent i = new Intent(this,DirectMessageList.class);
        i.putExtra(Preferences.DM_TYPE, 0);
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_dm_in")
        			.setIndicator(getString(R.string.dm_in))
        				.setContent(i));
        
        i = new Intent(this,DirectMessageList.class);
        i.putExtra(Preferences.DM_TYPE, 1);
        mTabHost.addTab(
        		mTabHost.newTabSpec("tab_dm_out")
        			.setIndicator(getString(R.string.dm_out))
        				.setContent(i));

        mTabHost.setCurrentTab(0);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.dm_option, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dm_new:
			DirectMessageNew.actionCompose(this);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
