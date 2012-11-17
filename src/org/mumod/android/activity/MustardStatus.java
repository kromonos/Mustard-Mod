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

import java.util.ArrayList;

import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.statusnet.Status;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MustardStatus extends MustardUserBaseActivity {

	
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
//		R_ROW_ID=R.layout.timeline_list_item_user;

		try {
			if (mStatusNet != null) {
				ArrayList<Status> u = null;
				u=mStatusNet.getStatus(DB_ROW_EXTRA);
				if (u!=null) {
					mUser = u.get(0).getUser();
					mUsername=u.get(0).getUser().getName();
					prepareUserView();
					getStatuses();
				} else {
					new AlertDialog.Builder(this)
					.setTitle(getString(R.string.error))
					.setMessage(getString(R.string.error_generic))
					.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface xdialog, int id) {
							finish();
						}
					}).show();
				}
			}
		} catch (Exception e) {
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.error))
			.setMessage(getString(R.string.error_generic_detail,e.getMessage() == null ? e.toString() : e.getMessage() ))
			.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					finish();
				}
			}).show();
		}
	}

	@Override
	protected void onBeforeFetch() {
		Intent intent = getIntent();
		Uri data = intent.getData();
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_SINGLE;
		DB_ROW_EXTRA=data.getLastPathSegment();
		isRefreshEnable=false;
		isBookmarkEnable=false;
	}

	@Override
	protected void onAfterFetch() {
	}

	@Override
	protected void onSetListView() {
//		if(mLayoutLegacy) {
			setContentView(R.layout.legacy_user_list);
			R_ROW_ID=R.layout.legacy_timeline_list_item_user;			
//		} else {
//			setContentView(R.layout.user_list);
//			R_ROW_ID=R.layout.timeline_list_item_user;
//		}
	}
	
}
