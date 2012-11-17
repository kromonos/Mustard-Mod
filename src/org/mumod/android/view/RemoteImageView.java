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

package org.mumod.android.view;

import org.mumod.android.Controller;
import org.mumod.android.MessagingListener;
import org.mumod.android.MustardApplication;
import org.mumod.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RemoteImageView extends ImageView {

	private String mRemote;
	private Context mContext;
	private int mResource = R.drawable.nullavatar;
	
	public RemoteImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		setImageResource(R.drawable.nullavatar);
		mContext=context;
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setImageResource(R.drawable.nullavatar);
		mContext=context;
	}

	public void setRemoteURI(String uri) {
		if (uri.startsWith("http")) {
			mRemote = uri;
		}
	}

	public void loadImage(int resource) {
		mResource=resource;
		if (mRemote != null) {
			if (MustardApplication.sImageManager.contains(mRemote)) {
				setFromLocal();
			} else {
				setImageResource(resource);
				doImageDownload();
			}
		}
	}
	
	public void loadImage() {
		loadImage(mResource);
	}
	
	private void doImageDownload() {
		new Thread() {
            public void run() {
            	Controller.getInstance(mContext)
            		.loadRemoteImage(mContext, mRemote, mListener);
            }
		}.start();
	}
	
	private void setFromLocal() {
		Bitmap bm = MustardApplication.sImageManager.get(mRemote);
		if(bm != null)
			setImageBitmap(bm);
		else {
			setImageResource(mResource);
		}
	}
	
	private void endLoadRemote() {
		Bitmap bm = MustardApplication.sImageManager.get(mRemote);
		if(bm != null)
			setImageBitmap(bm);
//		else {
//			Toast.makeText(mContext, mContext.getString(R.string.error_generic), Toast.LENGTH_LONG).show();
//		}
	}

	private RemoteImageHandler mHandler = new RemoteImageHandler();
	
	class RemoteImageHandler extends Handler {

		private static final int MSG_DOWNLOADED = 2;
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DOWNLOADED:
				endLoadRemote();
				break;
			}
		}

		public void imageDownloaded() {
			sendEmptyMessage(MSG_DOWNLOADED);
		}
	
	}
	
	private MessagingListener mListener = new MessagingListener() {
		public void loadRemoteImageFinished(Context context) {
	    	mHandler.imageDownloaded();
	    }
	};

}