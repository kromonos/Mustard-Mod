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

import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class MustardTag extends MustardBaseActivity {
	
	private static final String EXTRA_TAG = "group";
	
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		
		if (mStatusNet != null) {
			TextView tagInfo = (TextView) findViewById(R.id.tag_info);
			tagInfo.setText(getString(R.string.w_tag_title, DB_ROW_EXTRA));
			getStatuses();
			fillData();
		}

	}

	@Override
	protected void onBeforeFetch() {
		Intent intent = getIntent();
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_TAG;
		if(intent.hasExtra(EXTRA_TAG)) {
			DB_ROW_EXTRA=intent.getExtras().getString(EXTRA_TAG);
		} else {
			Uri data = intent.getData();
			DB_ROW_EXTRA=data.getLastPathSegment();
		}
		
	}
	
	@Override
	protected void onAfterFetch() {
	}

	@Override
	protected void onSetListView() {
		if(mLayoutLegacy)
			setContentView(R.layout.legacy_tag_list);
		else
			setContentView(R.layout.tag_list);
	}
	
	public static void actionHandleTimeline(Context context,String tag) {
		Intent intent = new Intent(context, MustardTag.class);
		intent.putExtra(EXTRA_TAG, tag);
	    context.startActivity(intent);
	}
	
}
