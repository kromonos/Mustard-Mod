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
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FilterSettings extends ListActivity {

	protected static final String TAG = "Filter";

	private Cursor mFiltersCursor;
	private MustardDbAdapter mDbHelper;
	
    private EditText mFilterText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters_list);
        mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		
        mFilterText = (EditText) findViewById(R.id.filter_text);
        
        Button searchButton = (Button) findViewById(R.id.button_add_filter);
        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                
            	String filter = mFilterText.getText().toString().trim();
            	if (filter!=null && !"".equals(filter)) {
            		addFilter(filter);
            		mFilterText.setText("");
            	}
            }
          
        });
        
        doPopulateList();
    }

    private void addFilter(String filter) {
    	try {
	    	mDbHelper.createFilter(filter);
	    	doPopulateList();
    	} catch (MustardException e) {
    		Log.e(TAG, e.getMessage());
    	}
    }
    
	private void doPopulateList() {
		
		if (mFiltersCursor!=null) {
			if(mFiltersCursor.requery()) {
				return;
			}
		}
		String[] from = new String[] {
				MustardDbAdapter.KEY_FILTER
		};
		int[] to = new int[] {
				R.id.filter_text
		};

		mFiltersCursor = mDbHelper.fetchFilters();

		if (mFiltersCursor == null) {
			Log.e(TAG, "Cursor is null.. ");
			return;
		}

		startManagingCursor(mFiltersCursor);
		SimpleCursorAdapter filters = new SimpleCursorAdapter(this,R.layout.filter_row,mFiltersCursor,from,to);
		setListAdapter(filters);
		registerForContextMenu(getListView());

	}
    
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		onDeleteBookmarks(info.id);
	}
	
	public boolean onDeleteBookmarks(final long bid) {
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.msg_delete_filter))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface xdialog, int id) {
				mDbHelper.deleteFilter(bid);
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
		
    
	public void onDestroy() {
		super.onDestroy();
		if(mDbHelper != null)
			mDbHelper.close();
	}
}
