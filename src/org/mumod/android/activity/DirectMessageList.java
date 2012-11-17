package org.mumod.android.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mumod.android.Controller;
import org.mumod.android.MessagingListener;
import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.Preferences;
import org.mumod.android.R;
import org.mumod.android.provider.StatusNet;
import org.mumod.android.view.ActionItem;
import org.mumod.android.view.GimmeMoreListView;
import org.mumod.android.view.MustardStatusTextView;
import org.mumod.android.view.QuickAction;
import org.mumod.android.view.RemoteImageView;
import org.mumod.statusnet.DirectMessage;
import org.mumod.util.DateUtils;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class DirectMessageList extends ListActivity implements GimmeMoreListView.OnNeedMoreListener {

	protected final String TAG = "Mustard/DirectMessage" ;

	protected Context mContext;
	protected SharedPreferences mPreferences;
	protected int mTextSizeNormal=14;
	protected int mTextSizeSmall=12;
	protected boolean mLayoutLegacy=false;
	protected StatusNet mStatusNet = null;
	protected long mStatusNetAccountId = -1;
	protected ArrayList<DirectMessage> mDirectMessages = new ArrayList<DirectMessage>();
	protected Controller mController;
	private DirectMessagesListHandler mDirectMessagesHandler = new DirectMessagesListHandler();
	private boolean mLoading = false;
	private boolean mEndReached = false;
	private QuickAction mQuickAction;
	private int mInOut = -1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		mContext = this;

		mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		String sFontSize=mPreferences.getString(Preferences.FONT_SIZE, "1");
		//		Log.i(TAG,"Font Size: " + sFontSize);
		int fontSize=1;
		try {
			fontSize=Integer.parseInt(sFontSize);
		} catch (NumberFormatException e) {
			// Not sure but got a cast exception..
			if(sFontSize.equals(getString(R.string.small))) {
				fontSize=0;
			} else if (sFontSize.equals(getString(R.string.medium))) {
				fontSize=1;
			} else {
				fontSize=2;
			}
		}

		mInOut = getIntent().getIntExtra(Preferences.DM_TYPE, 0);
		switch (fontSize) {
		case 0:
			mTextSizeNormal=12;
			mTextSizeSmall=10;
			break;
		case 1:
			mTextSizeNormal=14;
			mTextSizeSmall=12;
			break;
		case 2:
			mTextSizeNormal=16;
			mTextSizeSmall=14;
			break;
		}

		mLayoutLegacy = 
			mPreferences.getString(Preferences.THEME, getString(R.string.theme_bw))
			.equals(getString(R.string.theme_bw));
		
		if(mLayoutLegacy) {
			setContentView(R.layout.legacy_dents_list);
		} else {
			setContentView(R.layout.dents_list);
		}
		
		mController = Controller.getInstance(this);
		
		((GimmeMoreListView)getListView()).setOnNeedMoreListener(this);
		
		getStatusNet();
		getDirectMessages(0,true);
	}
	
	public void onResume() {
		super.onResume();

		NotificationManager notifMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifMgr.cancel(1);

		refresh();
	}
	
	
	private void getStatusNet() {
		MustardApplication _ma = (MustardApplication) getApplication();
		MustardDbAdapter dbHelper = getDbAdapter();
		if (mStatusNetAccountId>=0) {
			mStatusNet = _ma.checkAccount(dbHelper,mStatusNetAccountId);
		} else {
			mStatusNet = _ma.checkAccount(dbHelper);
		}
		releaseDbAdapter(dbHelper);
	}
	
	protected void getDirectMessages(long id, boolean higher) {
		if(mLoading)
			return;
		mLoading=true;
		mController.loadDirectMessages(this, mStatusNet, id, higher, mInOut, mListener);
	}
	
	public void needMore() {
		if(mEndReached)
			return;
//		Log.d(TAG, "Loading more..");
		DirectMessage dm = mDirectMessages.get(mDirectMessages.size()-1);
		long lowest = dm.getId();
		getDirectMessages(lowest-1, false);
	}

	protected MustardDbAdapter getDbAdapter() {
		MustardDbAdapter dbAdapter = new MustardDbAdapter(this);
		dbAdapter.open();
		return dbAdapter;
	}
	
	protected void releaseDbAdapter(MustardDbAdapter dbAdapter) {
		try {
			if(dbAdapter!= null)
				dbAdapter.close();
		} catch(Exception e) {
		}
	}

	protected void refresh() {
		if(mDirectMessages == null) 
			return;
		ArrayList<DirectMessage> dms = new ArrayList<DirectMessage>();
		NoticeListAdapter _nla = (NoticeListAdapter)getListView().getAdapter();
		if( _nla == null) {
//			Log.d(TAG,"_nla is null");
			getListView().setAdapter(new NoticeListAdapter(dms));
			return;
		}
		
		int p = getListView().getLastVisiblePosition();
		
//		Log.d(TAG,"_nla is not null");
		_nla.clear();
		
		for (DirectMessage directMessage : mDirectMessages) {
			_nla.add(directMessage);
		}
		
		getListView().setSelection(p);
	}

	protected void showToastMessage(CharSequence message,boolean longView) {
		int popTime = longView ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		Toast.makeText(this,
				message,
				popTime).show();
	}
	
	protected void onBeforeSetAccount() {
		Intent intent = getIntent();
		Uri data = intent.getData();
		if (intent.hasExtra(Preferences.EXTRA_ACCOUNT)) {
			mStatusNetAccountId=intent.getLongExtra(Preferences.EXTRA_ACCOUNT,-1);
//			Log.d(TAG, "Got an EXTRA_ACCOUNT: " + mStatusNetAccountId);
		} else {
			if (data != null) {
//				Log.d(TAG, data.toString());
				List<String> segs = data.getPathSegments();
				if (segs.size()>1) {
					try {
						mStatusNetAccountId=Long.valueOf(segs.get(0));
//						Log.d(TAG, "Got an EXTRA_ACCOUNT: " + mStatusNetAccountId);
					} catch (NumberFormatException e) {

					}
				}
			}
		}
	}
	
	private void onShowDmMenu(View v, final String screen_name) {
		ActionItem share = new ActionItem();
		share.setTitle(getString(R.string.menu_reply));
		share.setIcon(getResources().getDrawable(R.drawable.icon_share));
		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DirectMessageNew.actionCompose(mContext, screen_name);
				mQuickAction.dismiss();
			}
		});
		
		mQuickAction = new QuickAction(v);
		
		mQuickAction.addActionItem(share);
		
		mQuickAction.show();
	}
	
	class NoticeListAdapter extends ArrayAdapter<DirectMessage> {

		class ViewHolder {
			RemoteImageView profile_image;
			TextView screen_name;
			TextView account_name;
			MustardStatusTextView status;
			TextView datetime;
		}

		public NoticeListAdapter(ArrayList<DirectMessage> statuses) {
			super(DirectMessageList.this, 0, statuses);
		}

		public void setLoading(boolean loading) {
			mLoading = loading;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			final DirectMessage status = getItem(position);
//			Log.d(TAG, status.toString());
			View v;
			if (convertView != null) {
				v = convertView;
			} else {
				v = getLayoutInflater().inflate(R.layout.legacy_dm_list_item, parent, false);
			}
//			Log.d(TAG, "newView");
			ViewHolder vh =  (ViewHolder)v.getTag();
			if(vh == null) {
				vh = new ViewHolder();
				try {
					vh.profile_image = (RemoteImageView)v.findViewById(R.id.profile_image);
				} catch (Exception e) {
				}

				vh.screen_name = (TextView)v.findViewById(R.id.screen_name);
				try {
					vh.account_name = (TextView)v.findViewById(R.id.account_name);
				} catch (Exception e) {

				}
				vh.status = (MustardStatusTextView)v.findViewById(R.id.status);
				Typeface tf = Typeface.createFromAsset(getAssets(),MustardApplication.MUSTARD_FONT_NAME);
				vh.status.setTypeface(tf);
				vh.datetime = (TextView)v.findViewById(R.id.datetime);
				vh.datetime.setTypeface(tf);
				v.setTag(vh);
			}

			if (vh.screen_name != null) {
				vh.screen_name.setText(status.getOtherScreenname());
				vh.screen_name.setTextSize(mTextSizeSmall);
			}
			
			if (vh.profile_image != null) {
				String profileUrl = status.getOtherImage();
				if (profileUrl != null && !"".equals(profileUrl)) {
					vh.profile_image.setRemoteURI(profileUrl);
					vh.profile_image.loadImage();
				}
			}
			Date d = status.getCreated_at();
			vh.datetime.setText(DateUtils.getRelativeDate( mContext, d ));
			vh.datetime.setTextSize(mTextSizeSmall);
			
			String sstatus = status.getText();
			if (sstatus.indexOf("<")>=0)
				sstatus=sstatus.replaceAll("<", "&lt;");
			if(sstatus.indexOf(">")>=0)
				sstatus=sstatus.replaceAll(">","&gt;");

			TextView tv = vh.status;
			tv.setText(Html.fromHtml(sstatus).toString(), BufferType.SPANNABLE);
			Linkify.addLinks(tv, Linkify.WEB_URLS);
			tv.setTextSize(mTextSizeNormal);
			
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onShowDmMenu(v, status.getOtherScreenname());
				}
			});
			return v;
		}



		public boolean isEmpty() {
			if (mLoading) {
				// We don't want the empty state to show when loading.
				return false;
			} else {
				return super.isEmpty();
			}
		}


	}

	private void setEndLoading() {
		mLoading=false;
    	mDirectMessagesHandler.changeProgressBar(false);
	}
	
	private MessagingListener mListener = new MessagingListener() {
		
	    public void loadDirectMessagesStarted() {
	    	mDirectMessagesHandler.changeProgressBar(true);
	    }
	    
	    public void loadDirectMessagesFinished(ArrayList<DirectMessage> dms,int versus) {
	    	setEndLoading();
	    	mDirectMessagesHandler.dataRefresh(dms, versus);
	    }

	    public void loadDirectMessagesFailed(String reason) {
	    	setEndLoading();
	    	mDirectMessagesHandler.showError(reason);
	    }
		
	};
	
	class DirectMessagesListHandler extends Handler {

		private static final int MSG_PROGRESS = 1;
		private static final int MSG_DATA_REFRESH = 2;
		private static final int MSG_SHOW_ERROR = 3;
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS:
				setProgressBarIndeterminateVisibility(msg.arg1 != 0);
				break;
			case MSG_DATA_REFRESH:
				ArrayList<DirectMessage> dms = (ArrayList<DirectMessage>)msg.obj;
				if (msg.arg1 == 0) {
					mDirectMessages.addAll(0,dms); 
				} else {
					if(dms.size()==0) {
						// If we are scrolling and array is empty..
						mEndReached=true;
					}
					mDirectMessages.addAll(dms); 
				} 
				refresh();
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

		public void dataRefresh(ArrayList<DirectMessage> dms, int upDown) {
			Message msg = new Message();
			msg.what = MSG_DATA_REFRESH;
			msg.arg1 = upDown;
			msg.obj = dms;
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
