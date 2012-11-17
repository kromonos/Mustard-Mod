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

import java.io.File;
import java.io.FileOutputStream;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.R;
import org.mumod.android.provider.StatusNet;
import org.mumod.util.ImageUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Avatar extends Activity {

	private static final String TAG = "Avatar";

	private static final int ACCOUNT_ADD = 2;

	private final int CHOOSE_FILE_ID=0;

	private MustardDbAdapter mDbHelper;

	private StatusNet mStatusNet;
	private TextView mTextViewFileName;
	private ImageView mAvatar;
	private Bitmap mBitmapAvatar = null;
	private File mFilename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar);
		
		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();

		MustardApplication _ma = (MustardApplication) getApplication();
		mStatusNet = _ma.checkAccount(mDbHelper);

		if (mStatusNet == null) {
			if (MustardApplication.DEBUG) Log.i(TAG, "No account found. Starting Login activity");
			showLogin();
			return;
		}

		
		
		mAvatar=(ImageView) findViewById(R.id.avatar);
		
		mAvatar.setImageResource(R.drawable.avatar);

		mTextViewFileName = (TextView) findViewById(R.id.filename);
		
		Button selectFileBtn = (Button) findViewById(R.id.selectfilename);
		
		selectFileBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				showFileChooser();
			}        
		});
			
		
		Button uploadBtn = (Button) findViewById(R.id.upload);

		uploadBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (mFilename == null) {
					return;
				}
				new AvatarUpdater().execute("");
				setResult(RESULT_OK);
				finish();
			}

		});
	}


	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_FILE_ID);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == CHOOSE_FILE_ID) {
			if (resultCode == RESULT_OK) {
				Uri uri = intent.getData();
				if (uri != null) {
					Cursor cursor = getContentResolver().query(uri, null, null, null,null);
					if (cursor != null) {
						if(cursor.moveToFirst()) {
							mFilename = new File( cursor.getString(cursor
									.getColumnIndexOrThrow(ImageColumns.DATA)));
							new AvatarEditor().execute("");
						}
						cursor.close();
					}
				}
			}
		}
	}
	
	private void onAvatarSelect() {
		mAvatar.setImageBitmap(mBitmapAvatar);
		mTextViewFileName.setText(mFilename.getName());
	}
	
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG,"onCreateDialog");
		ProgressDialog dialog;
    	dialog = new ProgressDialog(this);
    	dialog.setIndeterminate(true);
    	dialog.setCancelable(false);
    	dialog.setMessage(getString(R.string.please_wait_resizing));
	    return dialog;
	}

	public void onDestroy() {
		super.onDestroy();
		if(mDbHelper != null)
			mDbHelper.close();
	}

	private void showLogin() {
		Intent i = new Intent(this, Login.class);
		startActivityForResult(i, ACCOUNT_ADD);
	}
	
	public class AvatarEditor extends AsyncTask<String, Integer, Integer> {

		private final String TAG = getClass().getCanonicalName();

		@Override
		protected Integer doInBackground(String... s) {
			if (MustardApplication.DEBUG) Log.i(TAG, "background task - start");
			
			try {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 10;
				Bitmap bm = BitmapFactory.decodeFile(mFilename.getAbsolutePath(),options);
				
				mBitmapAvatar = ImageUtil.crop(bm, 500, 500);
				bm.recycle();
				if(mBitmapAvatar==null) {
					return -1;
				}
				String avatarFile = "avatar.jpg";
				if (MustardApplication.DEBUG) Log.d(TAG, "filename: " + avatarFile);
				FileOutputStream fos = openFileOutput(avatarFile,MODE_WORLD_READABLE);
				mBitmapAvatar.compress(CompressFormat.JPEG, 50, fos);
				fos.flush();
				fos.close();
				mFilename = getFileStreamPath(avatarFile);

			} catch(Exception e) {
				e.printStackTrace();
				if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
				return 0;
			} finally {
				if (MustardApplication.DEBUG) Log.i(TAG, "background task - end ");
			}
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(0);
		}

		protected void onPostExecute(Integer result) {
			
			dismissDialog(0);
			
			if (result>0) {
				onAvatarSelect();
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.avatar_resize_ko),
		                   Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class AvatarUpdater extends AsyncTask<String, Integer, Integer> {

		private final String TAG = getClass().getCanonicalName();
				
		@Override
		protected Integer doInBackground(String... s) {
			if (MustardApplication.DEBUG) Log.i(TAG, "background task - start");
			
			try {
				mStatusNet.updateAvatar(mFilename);
			} catch(Exception e) {
				e.printStackTrace();
				if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
				return 0;
			} finally {
				if (MustardApplication.DEBUG) Log.i(TAG, "background task - end ");
			}
			return 1;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected void onPostExecute(Integer result) {

			try {
				if (result>0) {
					Toast.makeText(
							getApplicationContext(),
			                   getString(R.string.avatar_update_ok),
			                   Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.avatar_update_ko),
			                   Toast.LENGTH_LONG).show();
				}
			} catch(IllegalArgumentException e) {
				if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			} finally {				
			}
		}
	}
	
	public static void actionAvatar(Context context) {
		Intent intent = new Intent(context, Avatar.class);
	    context.startActivity(intent);
	}
	
}