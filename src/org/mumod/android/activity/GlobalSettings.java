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

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.Preferences;
import org.mumod.android.R;
import org.mumod.android.service.MultiMention;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class GlobalSettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	MustardDbAdapter mDbHelper;
	CharSequence[] items;
	long[] rowIds;
	boolean[] itemsSelected;

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mDbHelper != null) {
			mDbHelper.close();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		CheckBoxPreference chckUpd = (CheckBoxPreference)findPreference(Preferences.CHECK_UPDATES_KEY);
		chckUpd.setOnPreferenceChangeListener(this);

		ListPreference chckUpdInterval = (ListPreference)findPreference(Preferences.CHECK_UPDATE_INTERVAL_KEY);
		chckUpdInterval.setOnPreferenceChangeListener(this);

		mDbHelper = new MustardDbAdapter(this);
		mDbHelper.open();
		Cursor c = mDbHelper.fetchAllNonDefaultAccounts();
		items = new CharSequence[c.getCount()];
		rowIds = new long[c.getCount()];
		itemsSelected = new boolean [c.getCount()];
		int cc=0;
		while(c.moveToNext()) {
			items[cc]=c.getString(c.getColumnIndex(MustardDbAdapter.KEY_INSTANCE)) +
			"/" + c.getString(c.getColumnIndex(MustardDbAdapter.KEY_USER));
			rowIds[cc]=c.getLong(c.getColumnIndex(MustardDbAdapter.KEY_ROWID));
			itemsSelected[cc]=c.getInt(c.getColumnIndex(MustardDbAdapter.KEY_TO_MERGE)) == 1 ? true : false;
			cc++;
		}
		c.close();
		if(cc>0) {
			CheckBoxPreference chckMultiple = (CheckBoxPreference)findPreference(Preferences.CHECK_MERGED_TL_KEY);
			chckMultiple.setEnabled(true);
			chckMultiple.setOnPreferenceChangeListener(this);
		}

	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {

		if(MustardApplication.DEBUG)
			Log.i(getClass().getCanonicalName(), "A Preference is changed: " + preference.toString());

		final String key = preference.getKey();

		try {
			if (key.equals(Preferences.CHECK_UPDATES_KEY)) {
				if(MustardApplication.DEBUG) Log.i(getClass().getCanonicalName(), "Check update preference changed");
				boolean b = (Boolean)newValue;
				if(b) {
					if(MustardApplication.DEBUG) Log.i(getClass().getCanonicalName(), "Check update preference changed >> TRUE");
					MultiMention.schedule(getBaseContext());
				} else {
					if(MustardApplication.DEBUG) Log.i(getClass().getCanonicalName(), "Check update preference changed >> FALSE");
					MultiMention.unschedule(getBaseContext());
				}
			} else if (key.equals(Preferences.CHECK_UPDATE_INTERVAL_KEY)) {
				if(MustardApplication.DEBUG) Log.i(getClass().getCanonicalName(), "Check update interval preference changed");
				MultiMention.unschedule(getBaseContext());
				String sValue = (String)newValue;
				MultiMention.schedule(getBaseContext(),Integer.valueOf(sValue));
			} else if (key.equals(Preferences.CHECK_MERGED_TL_KEY)) {
				boolean b = (Boolean)newValue;
				if(b) {
					if(MustardApplication.DEBUG)
						Log.i(getClass().getCanonicalName(), "Check merge TL changed >> TRUE");
					buildAlertAccount();

				} else {
					if(MustardApplication.DEBUG)
						Log.i(getClass().getCanonicalName(), "Check merge TL changed >> FALSE");
//					Not sure if it's the right way, so I will comment it out
//					mDbHelper.resetMergedAccounts();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return true;
	}
	
	private void buildAlertAccount() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_merge_account_title);
		builder.setMultiChoiceItems(items, itemsSelected, new DialogInterface.OnMultiChoiceClickListener() {

			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(MustardApplication.DEBUG)
					Log.i(getClass().getCanonicalName(), "Account " + rowIds[which] + " changed >> " + isChecked);
				mDbHelper.setMergedAccount(rowIds[which],isChecked);
			}

		}).setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
		builder.create();
		builder.show();
	}

}
