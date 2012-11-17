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

package org.mumod.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mumod.android.R;

import android.content.Context;

public class DateUtils {

	public static String getRelativeDate(Context context, Date date) {
		
		Date now = new Date();
		// Seconds.
		float diff = (now.getTime() - date.getTime()) / 1000;
		if (diff < 60) {
			return context.getString(R.string.few_seconds);
		} else if (diff < 92) {
			return context.getString(R.string.one_minute_ago);
		} else if (diff < 3300) {
			return context.getString(R.string.minutes_ago, Math.round(diff/60));
		} else if (diff < 5400) {
			return context.getString(R.string.one_hour_ago);
		} else if (diff < 22 * 3600) {
			return context.getString(R.string.hours_ago, Math.round(diff/3600));
		} else if (diff < 37 * 3600) {
			return context.getString(R.string.one_day_ago);
		} else if (diff < 24 * 24 * 3600) {
			return context.getString(R.string.days_ago, Math.round(diff/(24*3600)));
		} else if (diff < 46 * 24 * 3600) {
			return context.getString(R.string.one_month_ago);
		} else if (diff < 330 * 24 * 3600) {
			return context.getString(R.string.months_ago, Math.round(diff/(30*24*3600)));
		} else if (diff < 480 * 24 * 3600) {
			return context.getString(R.string.one_year_ago);
		} else {
			return new SimpleDateFormat("d MM yyyy").format(date);
		}				
	}

}