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
import org.mumod.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Search extends Activity {

	protected static final String TAG = "Search";

	private MustardDbAdapter mDbHelper;
	
    private EditText mSearchText;
    
    private RadioButton mRadioUsers;
    private RadioButton mRadioGroups;
    private RadioButton mRadioNotice;
    private RadioButton mRadioTag;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dent_search);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        mRadioUsers = (RadioButton) findViewById(R.id.radio_users);
        mRadioGroups = (RadioButton) findViewById(R.id.radio_groups);
        mRadioNotice = (RadioButton) findViewById(R.id.radio_notice);
        mRadioTag = (RadioButton) findViewById(R.id.radio_tags);
        
        mSearchText = (EditText) findViewById(R.id.textSearch);

        mRadioUsers.toggle();
        
        Button searchButton = (Button) findViewById(R.id.btnSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	doSearch();
            	
            	
            	
            }
          
        });
    }

    private void doSearch() {
    	

    	String search = mSearchText.getText().toString().trim();
    	if (search!=null && !"".equals(search)) {
    		if(mRadioUsers.isChecked()) {
//    			Intent i = new Intent("android.intent.action.VIEW",Uri.parse("statusnet://users/"+search));
//    			startActivity(i);
    			MustardUser.actionHandleTimeline(this, search);
    		} else if (mRadioGroups.isChecked() ) {
    			MustardGroup.actionHandleTimeline(this, search);
    		} else if (mRadioNotice.isChecked()) {
//    			Intent i = new Intent("android.intent.action.VIEW",Uri.parse("statusnet://search/"+search));
//    			startActivity(i);
    			MustardSearch.actionHandleTimeline(this,search);
    		} else if (mRadioTag.isChecked()) {
//    			Intent i = new Intent("android.intent.action.VIEW",Uri.parse("statusnet://tags/"+search));
//    			startActivity(i);
        		MustardTag.actionHandleTimeline(this, search);
    		} else {
    			new AlertDialog.Builder(Search.this)
                .setTitle("Error")
                .setMessage("Ehm... don't know how.. but you hack the radiogroup :(")
                .setNeutralButton("Close", null).show();
    			return;
    		}
    	}
        finish();
    }
    
	public void onDestroy() {
		super.onDestroy();
		if(mDbHelper != null)
			mDbHelper.close();
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
            break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
