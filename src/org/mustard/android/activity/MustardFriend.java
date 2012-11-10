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
import org.mustard.util.MustardException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MustardFriend extends MustardUserBaseActivity {

	//private static final String EXTRA_USER="friend.user";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		if(mStatusNet!=null) {
//			TextView tagInfo = (TextView) findViewById(R.id.dent_info);
//			tagInfo.setText(getString(R.string.timeline_friends, DB_ROW_EXTRA));
//			new UserLoader().execute();
//			getStatuses();
			
			try {
				mUser = mStatusNet.getUser(DB_ROW_EXTRA);

				if (mUser == null) {
					new AlertDialog.Builder(MustardFriend.this)
					.setTitle(getString(R.string.warning))
					.setMessage(getString(R.string.error_user_not_found,DB_ROW_EXTRA))
					.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface xdialog, int id) {
							finish();
						}
					}).show();
				} else {
					prepareUserView();
					getStatuses();
				}
			} catch (MustardException e) {
				if(e.getCode()==404) {
					new AlertDialog.Builder(MustardFriend.this)
					.setTitle(getString(R.string.warning))
					.setMessage(getString(R.string.error_user_not_found,DB_ROW_EXTRA))
					.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface xdialog, int id) {
							finish();
						}
					}).show();		
				} else {
					e.printStackTrace();
					new AlertDialog.Builder(MustardFriend.this)
					.setTitle(getString(R.string.error))
					.setMessage(getString(R.string.error_generic_detail,e.getMessage() == null ? e.toString() : e.getMessage()))
					.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface xdialog, int id) {
							finish();
						}
					}).show();				
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				new AlertDialog.Builder(MustardFriend.this)
				.setTitle(getString(R.string.error))
				.setMessage(getString(R.string.error_generic_detail,e.getMessage() == null ? e.toString() : e.getMessage()))
				.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface xdialog, int id) {
						finish();
					}
				}).show();				
			}
		}
			
	}

	@Override
	protected void onBeforeFetch() {
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_FRIENDS;
		
		Intent intent = getIntent();

		String userid = intent.getStringExtra(EXTRA_USER);
		if(userid != null)
			DB_ROW_EXTRA=userid;
//		Uri data = intent.getData();
//		DB_ROW_EXTRA=data.getLastPathSegment();
		if(DB_ROW_EXTRA==null) {
			DB_ROW_EXTRA=Long.toString(mStatusNet.getUsernameId());
		}
	}

	@Override
	protected void onAfterFetch() {		
	}

	@Override
	protected void onSetListView() {
		if(mLayoutLegacy)
			setContentView(R.layout.legacy_friend_list);
		else
			setContentView(R.layout.friend_list);
	}
	
	public static void actionHandleTimeline(Context context,String userid) {
		Intent intent = new Intent(context, MustardFriend.class);
		intent.putExtra(EXTRA_USER, userid);
	    context.startActivity(intent);
	}
	
	public static Intent getActionHandleTimeline(Context context,long userid) {
		Intent intent = new Intent(context, MustardFriend.class);
		intent.putExtra(EXTRA_USER, userid);
	    return intent;
	}

}
