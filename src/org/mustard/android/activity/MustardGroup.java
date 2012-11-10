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
import org.mustard.android.view.RemoteImageView;
import org.mustard.statusnet.Group;
import org.mustard.util.MustardException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MustardGroup extends MustardBaseActivity {
	
	private static final String EXTRA_GROUP = "group";
	
	private static final int K_MEMBER_UNKOWN = 0;
	private static final int K_IS_MEMBER = 1;
	private static final int K_NOT_MEMBER = 2;
	private Group mGroup =  null;
	private int isGroupMemeber = K_MEMBER_UNKOWN;
	
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getCanonicalName();
		super.onCreate(savedInstanceState);
		
		if (mStatusNet != null) {
			StatusNetGroupFetcher sngf = new StatusNetGroupFetcher();
			sngf.execute();
		}
	}

	private void prepareGroupView() {
		RemoteImageView mProfileImage = (RemoteImageView) findViewById(R.id.group_image);
		TextView mGroupFullName = (TextView) findViewById(R.id.group_fullname);
		mGroupFullName.setText(mGroup.getFullname());
		
		TextView mGroupInfo = (TextView) findViewById(R.id.group_info);
		mGroupInfo.setText(mGroup.getDescription());

		String profileImageUrl = mGroup.getStream_logo();
		Log.d(TAG,"profileImageUrl: " + profileImageUrl);
		if (profileImageUrl != null && !"".equals(profileImageUrl) && !"null".equalsIgnoreCase(profileImageUrl)) {
			try {
				mProfileImage.setRemoteURI(profileImageUrl);
				mProfileImage.loadImage();
//				MustardApplication.sImageManager.put(profileImageUrl);
//				mProfileImage.setImageBitmap(MustardApplication.sImageManager
//						.get(profileImageUrl));
			} catch (Exception e) {
				if (MustardApplication.DEBUG) Log.e(TAG, "Can't fetch: <"+profileImageUrl+"> " + e.toString());
			}
		}
	}

	@Override
	protected void onBeforeFetch() {
		Intent intent = getIntent();
		DB_ROW_TYPE=MustardDbAdapter.ROWTYPE_GROUP;		
		if (intent.hasExtra(EXTRA_GROUP)) {
			DB_ROW_EXTRA=intent.getExtras().getString(EXTRA_GROUP);			
		} else {
			Uri data = intent.getData();
			DB_ROW_EXTRA=data.getLastPathSegment();
		}
	}
	
	private void onPostGroupMemberCheck() {
		invalidateOptionsMenu();
	}
	
	private void onPostGroupFetch() {
		if (mGroup == null) {
			new AlertDialog.Builder(MustardGroup.this)
			.setTitle(R.string.warning)
			.setMessage(getString(R.string.error_group_not_found,DB_ROW_EXTRA))
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface xdialog, int id) {
					finish();
				}
			}).show();
		} else {
			DB_ROW_EXTRA = mGroup.getNickname();
			StatusNetGroupMemberChecker sngmc = new StatusNetGroupMemberChecker();
			sngmc.execute();
			prepareGroupView();
			getStatuses();
		}
	}

	@Override
	protected void onAfterFetch() {		
	}

	@Override
	protected void onSetListView() {
		if(mLayoutLegacy)
			setContentView(R.layout.legacy_group_list);
		else
			setContentView(R.layout.group_list);
	}
	
	protected void onPreCreateOptionsMenu(Menu menu) {
		menu.add(0, GROUP_JOIN_ID, 0, R.string.menu_join)
		.setIcon(android.R.drawable.ic_menu_add);			
		menu.add(0, GROUP_LEAVE_ID, 0, R.string.menu_leave)
		.setIcon(android.R.drawable.ic_menu_delete);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if(isGroupMemeber == K_MEMBER_UNKOWN) {
			// If group membership request fails
			// Both menu items are shown
			menu.findItem(GROUP_JOIN_ID).setVisible(true);
			menu.findItem(GROUP_LEAVE_ID).setVisible(true);	
		} else {
			boolean isMember = isGroupMemeber == K_IS_MEMBER;
			menu.findItem(GROUP_JOIN_ID).setVisible(!isMember);
			menu.findItem(GROUP_LEAVE_ID).setVisible(isMember);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public static void actionHandleTimeline(Context context,String group) {
		Intent intent = new Intent(context, MustardGroup.class);
		intent.putExtra(EXTRA_GROUP, group);
	    context.startActivity(intent);
	}

	public class StatusNetGroupFetcher extends AsyncTask<Void, Integer, Integer> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(MustardBaseActivity.DIALOG_FETCHING_ID);
//			mHandler.progress(true);
		}

		@Override
		protected void onPostExecute(Integer result) {
//			mHandler.progress(false);
			try { dismissDialog(MustardBaseActivity.DIALOG_FETCHING_ID); } catch (IllegalArgumentException e) {}
			onPostGroupFetch();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				mGroup = mStatusNet.getGroup(DB_ROW_EXTRA);
				mStatusNet.isGroupMember(DB_ROW_EXTRA);			
			} catch (MustardException e) {

			}
			return 1;
		}
	}
	
	public class StatusNetGroupMemberChecker extends AsyncTask<Void, Integer, Integer> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mHandler.progress(true);
		}

		@Override
		protected void onPostExecute(Integer result) {
//			mHandler.progress(false);
			onPostGroupMemberCheck();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			boolean isMember =  mStatusNet.isGroupMember(DB_ROW_EXTRA);
			isGroupMemeber = isMember ? K_IS_MEMBER : K_NOT_MEMBER;
			return 1;
		}
	}
}
