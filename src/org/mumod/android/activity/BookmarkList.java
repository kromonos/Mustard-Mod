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

import org.mumod.android.MustardDbAdapter;
import org.mumod.android.Preferences;
import org.mumod.android.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class BookmarkList extends ListActivity {

	private static String TAG = BookmarkList.class.getCanonicalName();

	private Cursor mBookmarksCursor;
	private MustardDbAdapter mDbHelper;
	private int mBookmarkType;
	private long mUserId;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmarks_list);
		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		mBookmarkType = extras.getInt(Preferences.BOOKMARK_TYPE);
		mUserId = extras.getLong(Preferences.USERID);
		doPopulateList();
	}

	private void doPopulateList() {
		
		if (mBookmarksCursor!=null) {
			if(mBookmarksCursor.requery()) {
				return;
			}
		}
		String[] from = new String[] {
				MustardDbAdapter.KEY_BPARAM
		};
		int[] to = new int[] {
				R.id.bookmark_text
		};

		mBookmarksCursor = mDbHelper.fetchUserBookmarksByType(mUserId,mBookmarkType);

		if (mBookmarksCursor == null) {
			Log.e(TAG, "Cursor is null.. ");
			return;
		}

		startManagingCursor(mBookmarksCursor);
		SimpleCursorAdapter bookmarks = new SimpleCursorAdapter(this,R.layout.bookmark_row,mBookmarksCursor,from,to);
		setListAdapter(bookmarks);
		registerForContextMenu(getListView());

	}

	public void onDestroy() {
		if(mDbHelper != null) {
			mDbHelper.close();
		}
		super.onDestroy();
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String param = mDbHelper.fetchBookmark(id);

		switch(mBookmarkType) {
		case MustardDbAdapter.ROWTYPE_USER:
			MustardUser.actionHandleTimeline(this, param);
			break;

		case MustardDbAdapter.ROWTYPE_GROUP:
			MustardGroup.actionHandleTimeline(this, param);
			break;

		case MustardDbAdapter.ROWTYPE_TAG:
    		MustardTag.actionHandleTimeline(this, param);
			break;

		case MustardDbAdapter.ROWTYPE_SEARCH:
			MustardSearch.actionHandleTimeline(this,param);
			break;
		}

		mDbHelper.increaseBookmarkCounter(id);
		finish();


	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		onDeleteBookmarks(info.id);
	}
	
	public boolean onDeleteBookmarks(final long bid) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.msg_delete_bookmark))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface xdialog, int id) {
				mDbHelper.deleteBookmark(bid);
				doPopulateList();
			}
		})
		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface xdialog, int id) {
				xdialog.cancel();
			}
		});
		builder.create();
		builder.show();
		
		return true;
	}
		
	


}
