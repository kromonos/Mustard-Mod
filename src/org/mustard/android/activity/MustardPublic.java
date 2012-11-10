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

import org.mustard.android.MustardDbAdapter;
import org.mustard.android.R;
import org.mustard.android.view.GimmeMoreListView;

import android.os.Bundle;

public class MustardPublic extends MustardBaseActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		
		if( mStatusNet != null) {
			getStatuses();
			GimmeMoreListView view = (GimmeMoreListView)getListView();
			view.setOnNeedMoreListener(this);
//			fillData();

		}
	}

	@Override
	protected void onBeforeFetch() {
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_PUBLIC;
		DB_ROW_EXTRA="";
	}

	@Override
	protected void onAfterFetch() {		
	}

	@Override
	protected void onSetListView() {
		if(mLayoutLegacy)
			setContentView(R.layout.legacy_public_list);
		else
			setContentView(R.layout.public_list);
	}


	
}
