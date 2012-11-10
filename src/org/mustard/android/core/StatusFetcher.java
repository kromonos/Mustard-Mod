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
package org.mustard.android.core;

import java.util.ArrayList;

import org.mustard.android.MustardApplication;
import org.mustard.android.MustardDbAdapter;
import org.mustard.android.provider.StatusNet;

import android.util.Log;

public class StatusFetcher {

	private static final String TAG = "StatusFetcher";
	
	public int execute(MustardDbAdapter dbHelper, StatusNet statusNet, int rowType, String extra) throws Exception {
		if (MustardApplication.DEBUG) 
			Log.i(TAG, "background task - start");
		ArrayList<org.mustard.statusnet.Status> al = null;
		try {
			if (statusNet==null) {
				Log.e(TAG, "Statusnet is null!");
				return 0;
			}
			long maxId = dbHelper.fetchMaxStatusesId(0,rowType,extra);
			al=statusNet.get(rowType,extra,maxId,true);
			if(al==null || al.size()< 1) {
				return 0;
			} else {
				dbHelper.createStatuses(0,rowType,extra,al);
			}
		} catch(Exception e) {
			if (MustardApplication.DEBUG) 
				e.printStackTrace();
			throw e;
		} finally {
			if (MustardApplication.DEBUG) Log.i(TAG, "background task - end ");
		}
		return 1;
	}

}
