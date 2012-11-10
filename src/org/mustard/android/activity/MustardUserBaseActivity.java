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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.mustard.android.R;
import org.mustard.android.view.RemoteImageView;
import org.mustard.statusnet.User;
import org.mustard.util.MustardException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public abstract class MustardUserBaseActivity extends MustardBaseActivity {

	protected User mUser;
	protected String mUsername;
	
	protected void prepareUserView() {
		
		RemoteImageView profileImage = (RemoteImageView) findViewById(R.id.user_image);
		TextView userFullName = (TextView) findViewById(R.id.user_fullname);
		TextView userLocation  = (TextView) findViewById(R.id.user_location);
		TextView userInfo = (TextView) findViewById(R.id.user_info);
		TextView userSince = (TextView) findViewById(R.id.user_since);
		TextView userFollowers = (TextView) findViewById(R.id.user_followers);
		TextView userFollowing = (TextView) findViewById(R.id.user_subscribers);
		TextView userDents = (TextView) findViewById(R.id.user_dents);
		if (mUser.getName()!=null && !mUser.getName().equals("null"))
			userFullName.setText(mUser.getName());
		if (mUser.getDescription()!=null && !mUser.getDescription().equals("null"))
			userInfo.setText(mUser.getDescription());
		if (mUser.getLocation()!=null && !mUser.getLocation().equals("null"))
			userLocation.setText(mUser.getLocation());
		userFollowers.setText(getString(R.string.user_follower,mUser.getFollowers_count()));
		userFollowing.setText(getString(R.string.user_following,mUser.getFriends_count()));
		userDents.setText(getString(R.string.user_dents,mUser.getStatuses_count()));
		
		DateFormat df =  new SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH);
		userSince.setText(getString(R.string.user_since, df.format(mUser.getCreated_at())));
		String profileImageUrl = mUser.getProfile_image_url();

		if (profileImageUrl != null && !"".equals(profileImageUrl) && !"null".equalsIgnoreCase(profileImageUrl)) {
//			try {
				profileImage.setRemoteURI(profileImageUrl);
				profileImage.loadImage();
				
				final String bigProfileImageUrl = profileImageUrl.replace("-48-","-96-" ); 
				profileImage.setOnClickListener(new View.OnClickListener() {

        			public void onClick(View v) {
        				showAttachmentImage(bigProfileImageUrl,false);
        			}
        		});
		}
	}

	protected void getMentions() {
		MustardMention.actionHandleTimeline(this, mUser.getScreen_name());
	}
	
	protected void onPostUserFetch(int code, String message) {
		
		if(code == 0) {

			if (mUser == null) {
				new AlertDialog.Builder(MustardUserBaseActivity.this)
				.setTitle(getString(R.string.warning))
				.setMessage(getString(R.string.error_user_not_found,DB_ROW_EXTRA))
				.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface xdialog, int id) {
						finish();
					}
				}).show();
			} else {
				if(isRemoteTimeline) {
					DB_ROW_EXTRA=mUser.getProfile_json_url();
				}
				prepareUserView();
				getStatuses();
			}
		
		} else {
			if(code==404) {
				new AlertDialog.Builder(MustardUserBaseActivity.this)
				.setTitle(getString(R.string.warning))
				.setMessage(getString(R.string.error_user_not_found,DB_ROW_EXTRA))
				.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface xdialog, int id) {
						finish();
					}
				}).show();		
			} else {
				new AlertDialog.Builder(MustardUserBaseActivity.this)
				.setTitle(getString(R.string.error))
				.setMessage(getString(R.string.error_generic_detail,message))
				.setNeutralButton(R.string.close,  new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface xdialog, int id) {
						finish();
					}
				}).show();
			}
		}
	}
	
	public class StatusNetUserFetcher extends AsyncTask<Void, Integer, Integer> {
		
		private String mErrorMessage;
		private int mErrorCode;

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
			onPostUserFetch(mErrorCode,mErrorMessage);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				if(isRemoteTimeline) {
					mUser = mStatusNet.getRemoteUser(DB_ROW_EXTRA);
				} else {
					mUser = mStatusNet.getUser(DB_ROW_EXTRA);
				}
				mErrorCode = 0;
			} catch (MustardException e) {
				e.printStackTrace();
				mErrorCode = e.getCode();
				mErrorMessage = e.getMessage();
			}
			return 1;
		}
	}
}
