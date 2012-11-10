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


package org.mustard.android.provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mustard.geonames.GeoName;

public class GeoNamesJSONUtil {
	
	public static GeoName getGeoName(JSONObject o) {
		GeoName gn = new GeoName();
		if (o.has("geonames")) {
			try {
				JSONArray geonames = o.getJSONArray("geonames");
				if (geonames.length()>0) {
					// I work only on the first one
					JSONObject geoname = geonames.getJSONObject(0);
					long id = geoname.getLong("geonameId");
					gn.setGeonameId(id);
					if (geoname.has("countryName"))
						gn.setCountryName(geoname.getString("countryName"));
					
					if (geoname.has("name"))
						gn.setName(geoname.getString("name"));
					if (geoname.has("adminName1"))
						gn.setAdminName1(geoname.getString("adminName1"));
					if (geoname.has("adminName2"))
						gn.setAdminName2(geoname.getString("adminName2"));
					if (geoname.has("adminName3"))
						gn.setAdminName3(geoname.getString("adminName3"));
					if (geoname.has("adminName4"))
						gn.setAdminName4(geoname.getString("adminName4"));
					if (geoname.has("adminCode1"))
						gn.setAdminCode1(geoname.getString("adminCode1"));
					if (geoname.has("adminCode2"))
						gn.setAdminCode2(geoname.getString("adminCode2"));
					
//					Log.v("Mustard", "geonameId: " + id);
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return gn;
	}

}
