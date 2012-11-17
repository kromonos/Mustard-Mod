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

package org.mumod.android.provider;

import java.util.ArrayList;

import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;

import android.database.Cursor;
import android.util.Log;

public class OAuthLoader {
	
	public static final int OK=0;
	public static final int KO=2;
	public static final int PARTIAL=3;
	public static final int EMPTY=4;
	
	private MustardDbAdapter mDbAdapter;
	
	public  OAuthLoader (MustardDbAdapter dbAdapter) {
		mDbAdapter=dbAdapter;
	}

	/**
	 * Download keys and try to insert into DB
	 * @param olist
	 * @param replace
	 * @return
	 */
	public int load(ArrayList<OAuthInstance> olist,boolean replace) {
		boolean ok=false;
		boolean ko=false;
		if (MustardApplication.DEBUG) Log.d("OAuthManager","Got " + olist.size() + " keys");
		for (OAuthInstance o : olist) {
			boolean r = mDbAdapter.insertOauth(o.instance, o.key, o.secret,replace);
			if (r)
				ok=true;
			else
				ko=true;
			if (MustardApplication.DEBUG) Log.d("OAuthLoader","Inserting " + o.instance + " (" +  o.key + "/" +  o.secret + ") = " +r);
		}
		if (ok && !ko)
			return OK;
		else if (ok && ko)
			return PARTIAL;
		else
			return KO;
	}
	
	public OAuthInstance get(String instance) {
		OAuthInstance o = null;
		Cursor c = mDbAdapter.fetchOauth(instance);
		if (c.moveToNext()) {
			o = new OAuthInstance();
			o.id=c.getLong(c.getColumnIndexOrThrow(MustardDbAdapter.KEY_ROWID));
			o.instance=c.getString(c.getColumnIndexOrThrow(MustardDbAdapter.KEY_INSTANCE));
			o.key=c.getString(c.getColumnIndexOrThrow(MustardDbAdapter.KEY_KEY));
			o.secret=c.getString(c.getColumnIndexOrThrow(MustardDbAdapter.KEY_SECRET));
		} else {  
			o = new OAuthInstance();
			o.id=0;
			o.instance="*all*";
			o.key="anonymous";
			o.secret="anonymous";
		}
		try {
			if (c!=null) {
				c.close();
			}
		} catch (Exception e) {
		}
		return o;
	}
	
}
