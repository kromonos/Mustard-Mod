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

import org.json.JSONObject;
import org.mumod.android.MustardApplication;
import org.mumod.geonames.GeoName;
import org.mumod.util.HttpManager;
import org.mumod.util.MustardException;

import android.content.Context;
import android.util.Log;

public class GeoNames {

	private static final String TAG="GeoNames";
	
	private static final String API_GEONAME_URL = "http://ws.geonames.org";
	private static final String API_GEONAME_FIND_NEARBY = "/findNearbyJSON";
	
	public static GeoName getGeoName(Context context, String lon,String lat) throws MustardException {
		GeoName gn = null;
		
		String lURL = API_GEONAME_URL + API_GEONAME_FIND_NEARBY ;
		HttpManager hm = new HttpManager(context);

		lURL+="?lng="+lon+"&lat="+lat+"&fclass=P&fcode=PPLA&fcode=PPL&fcode=PPLC&style=full";
		JSONObject o = null;
//		Log.v(TAG,lURL);
		try {
			o = hm.getJsonObject(lURL);
//			Log.v(TAG, o.toString(1));
			gn = GeoNamesJSONUtil.getGeoName(o);
			gn.setLat(lat);
			gn.setLng(lon);
			if (MustardApplication.DEBUG)Log.d(TAG,o.toString());
		} catch (Exception e){
//			e.printStackTrace();
			if (MustardApplication.DEBUG)Log.e(TAG,e.toString());
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		
		return gn;
	}
	
}
