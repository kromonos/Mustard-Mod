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

import org.mustard.android.Controller;
import org.mustard.android.MessagingListener;
import org.mustard.android.MustardApplication;
import org.mustard.android.MustardDbAdapter;
import org.mustard.android.R;
import org.mustard.android.provider.StatusNet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DirectMessageNew  extends Activity {

	
	public static String EXTRA_SCREEN_NAME = "extScreen";
	
	public static void actionCompose(Context context,String screen_name) {
		Intent i = new Intent(context, DirectMessageNew.class);
		i.putExtra(EXTRA_SCREEN_NAME, screen_name);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	public static void actionCompose(Context context) {
		Intent i = new Intent(context, DirectMessageNew.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dm_new);
		
		EditText tv = (EditText)findViewById(R.id.dm_screen_name);
		
		String screen_name = getIntent().getStringExtra(EXTRA_SCREEN_NAME);
		if (screen_name != null) {
			tv.setText(screen_name);
			tv.setFocusable(false);
			((EditText)findViewById(R.id.dm_text)).setSelection(0);
		}
		
		Button sendButton = (Button) findViewById(R.id.send);
		sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				send();
				changeButtonStatus(false);
			}
		});
	}
	

	private void send() {
		EditText vtext = (EditText)findViewById(R.id.dm_text);
		String text = vtext.getText().toString().trim();
		if("".equals(text)) {
			return;
		}
		EditText vscreen = (EditText)findViewById(R.id.dm_screen_name);
		String screen_name = vscreen.getText().toString().trim();
		if("".equals(screen_name)) {
			return;
		}
		Controller c = Controller.getInstance(this);
		MustardDbAdapter dbHelper = getDbAdapter();
		StatusNet statusNet = ((MustardApplication) getApplication()).checkAccount(dbHelper);
		releaseDbAdapter(dbHelper);
		c.sendDirectMessage(this, statusNet, screen_name, text, mListener);
	}
	
	protected MustardDbAdapter getDbAdapter() {
		MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
		dbAdapter.open();
		return dbAdapter;
	}
	
	protected void releaseDbAdapter(MustardDbAdapter dbHelper) {
		try {
			if(dbHelper!= null)
				dbHelper.close();
		} catch(Exception e) {
		}
	}
	
	private DirectMessageHandler mDirectMessagesHandler = new DirectMessageHandler();
	
	private void setEndLoading() {
    	mDirectMessagesHandler.changeProgressBar(false);
	}
	
	protected void showToastMessage(CharSequence message,boolean longView) {
		int popTime = longView ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(this,
				message,
				popTime).show();
	}
	
	private void changeButtonStatus(boolean enable) {
		((Button) findViewById(R.id.send)).setEnabled(enable);
	}
	
	private MessagingListener mListener = new MessagingListener() {
		
	    public void sendDirectMessageStarted() {
	    	mDirectMessagesHandler.changeProgressBar(true);
	    }
	    
	    public void sendDirectMessageFinished() {
	    	setEndLoading();
	    	finish();
	    }

	    public void sendDirectMessageFailed(String reason) {
	    	setEndLoading();
	    	mDirectMessagesHandler.changeSendButton(true);
	    	mDirectMessagesHandler.showError(reason);
	    }
		
	};
	
	class DirectMessageHandler extends Handler {

		private static final int MSG_SEND_BUTTON = 1;
		private static final int MSG_PROGRESS = 2;
		private static final int MSG_SHOW_ERROR = 3;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS:
				setProgressBarIndeterminateVisibility(msg.arg1 != 0);
				break;
			case MSG_SEND_BUTTON:
				changeButtonStatus(msg.arg1 != 0);
				break;
			case MSG_SHOW_ERROR:
				showToastMessage((String)msg.obj,true);
				break;
			default:
				break;
			}
		}

		public void changeProgressBar(boolean progress) {
			Message msg = new Message();
			msg.what = MSG_PROGRESS;
			msg.arg1 = progress ? 1 : 0;
			sendMessage(msg);
		}

		public void changeSendButton(boolean progress) {
			Message msg = new Message();
			msg.what = MSG_SEND_BUTTON;
			msg.arg1 = progress ? 1 : 0;
			sendMessage(msg);
		}

		public void showError(String message) {
			Message msg = new Message();
			msg.what = MSG_SHOW_ERROR;
			msg.obj = message;
			sendMessage(msg);
		}

	}

}
