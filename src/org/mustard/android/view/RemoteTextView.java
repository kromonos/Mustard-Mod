/**
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

package org.mustard.android.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mustard.android.Controller;
import org.mustard.android.MessagingListener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;

public class RemoteTextView extends MustardStatusTextView {

	private String mRemote;
	private Context mContext;
	
	public RemoteTextView(Context context) {
		super(context);
		mContext=context;
	}

	public RemoteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}

	public void setRemoteURI(String uri, String alternateText) {
		if (uri.startsWith("http")) {
			mRemote = uri;
			doTextDownload();
		}
		setText(alternateText);
	}

	private void doTextDownload() {
		new Thread() {
            public void run() {
            	Controller.getInstance(mContext)
            		.loadRemoteText(mContext, mRemote, mListener);
            }
		}.start();
	}

	private RemoteTextHandler mHandler = new RemoteTextHandler();
	
	class RemoteTextHandler extends Handler {

		private static final int MSG_DOWNLOADED = 2;
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DOWNLOADED:
				setHTML((String)msg.obj);
				break;
			}
		}

		public void textDownloaded(String html) {
			Message msg = new Message();
			msg.what=MSG_DOWNLOADED;
			msg.obj=html;
			sendMessage(msg);
		}
	
	}
	
	private MessagingListener mListener = new MessagingListener() {
		
		public void loadRemoteTextStarted(Context context) {
			Log.v("RemoteTextView","loadRemoteTextStarted");
		}
		
		public void loadRemoteTextFinished(Context context, String html) {
			Log.v("RemoteTextView","loadRemoteTextFinished");
	    	mHandler.textDownloaded(html);
	    }
		
		public void loadRemoteTextFailed(Context context, String reason) {
			Log.v("RemoteTextView","loadRemoteTextFailed: " + reason);
	    }
	};

	public void setHTML(String html) {
		
		String expr = "<body>(.*?)</body>";
		Pattern patt = Pattern.compile(expr,
				  Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher m = patt.matcher(html);
		
		StringBuffer sbf = new StringBuffer();
		while (m.find()) {
			
			Log.v("RemoteTextView", m.group());
			sbf.append(m.group());
		}
		setText(Html.fromHtml(sbf.toString()));
	}
//		html=html.replaceAll("(\r\n|\n)","");
//		Spanned spannedHTML=Html.fromHtml(html);
//		SpannableString message=new SpannableString(spannedHTML.toString());
//		Object[] spans=spannedHTML.getSpans(0,spannedHTML.length
//				(),Object.class);
//		for (Object span: spans) {
//			int start=spannedHTML.getSpanStart(span);
//			int end=spannedHTML.getSpanEnd(span);
//			int flags=spannedHTML.getSpanFlags(span);
//			Log.v("RemoteTextView",span.getClass().getCanonicalName());
//			Log.v("RemoteTextView",span.toString());
//			Log.v("RemoteTextView","###############");
//			if (span instanceof URLSpan) {
//				URLSpan urlSpan=(URLSpan)span;
//				if (urlSpan.getURL().startsWith(CallbackSpan.PREFIX)) {
//					span=new CallbackSpan(urlSpan.getURL());
//				}
//			}
//			
//			message.setSpan(span,start,end,flags);
//		}
//		setText(message);
//	}
//	
//	protected void onCallback(String data) {
//    
//	}
//	
//	private final class CallbackSpan extends ClickableSpan {
//        public CallbackSpan(String url) {
//                int start=(url.startsWith(PREFIX)?PREFIX.length():0);
//                m_data=url.substring(start);
//        }
//        public void onClick(View view) {
//                onCallback(m_data);
//        }
//        public static final String PREFIX="callback:";
//        private String m_data;
//	}
		
}