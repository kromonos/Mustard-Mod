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

package org.mumod.android;

public class Preferences {

	public static final String CURRENT_VERSION = "cv";
	
	public static final String CHECK_MERGED_TL_KEY = "check_merge_timelines";
	public static final String CHECK_UPDATES_KEY = "check_updates";
	public static final String CHECK_UPDATE_INTERVAL_KEY =
		"check_update_interval";
	public static final String VIBRATE_KEY = "vibrate";
	public static final String REPLIES_ONLY_KEY = "replies_only";

	public static final String AUTO_REFRESH_KEY = "auto_refresh";
	public static final String AUTO_REFRESH_INTERVAL_KEY =
		"auto_refresh_interval";
	public static final String SEND_SNAPSHOT_KEY="send_snapshot";
	
	public static String THEME_KEY = "theme";
	
	public static String RINGTONE_KEY = "ringtone";
	public static final String RINGTONE_DEFAULT_KEY =
		"content://settings/system/notification_sound"; 

	public static final String FETCH_MAX_ITEMS_KEY="max_items_to_fetch";
	public static final int FETCH_MAX_ITEMS=25;

	public static final String GEOLOCATION_FUZZY_KEY="geolocation_fuzzy";
	public static final String GEOLOCATION_ENABLES_KEY="enable_geolocation";
	public static final String GEOLOCATION_ENABLE="GEOLOCATION_ENABLE";

	public static final String REFRESH_ON_POST_ENABLES_KEY="enable_refresh_on_post";
//	public static final String BOTTOM_BUTTONS="bottom_buttons";
	
	public static final String SPAMREPORT_ON_BLOCK="enable_spamreport_on_block";
	public static final String SPAMREPORT_USER="spam_user";
	public static final String SPAMREPORT_GROUP="spam_group";
	
//	public static final String NEW_REPEAT_ENABLES_KEY="enable_new_repeat";
	public static final String SHOW_NICKNAME_IN_REPLY_KEY="show_nick_reply";

	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	
	public static final String STATUS_IN_REPLY_TO="statusInReplyTo";
	public static final String STATUS_TEXT="statusText";
	public static final String STATUS_LOCATION="statusLocation";
	public static final String STATUS_FILE="statusFile";
	public static final String STATUS_TYPE="statusType";
	public static final String STATUS_ACCOUNT_ROWID="statusAccountRowId";
	
	public static final int STATUS_TYPE_REPLY=  1;
	public static final int STATUS_TYPE_REDENT= 2;
	
	public static final String USERID="userId";
	
	public static final String BOOKMARK_TYPE="btype";
	
	public static final String DM_TYPE="dmtype";
	
	public static  final int OSM=0;
	public static  final int GN=1;
	public static  final int GOOGLE=3;
	
//	public static final String LAYOUT_LEGACY="legacy_layout";
//	public static final String LAYOUT_NEW_BUTTON="new_button";
	
	public static final String LAST_VERSION_CHECK="last_version_check";
	
//	public static final String COMPACT_VIEW="compact_view";
	public static final String FONT_SIZE="font_size";
	public static final String URL_SHORTENER="url_shortener";
	public static final String THEME="theme";
//	public static final String LAYOUT_SHOW_CONTEXT="layout_show_context";
	
	
	public static final String EXTRA_ACCOUNT="mustard.account";
	public static final String EXTRA_USER="mustard.user";

	public static final String HIDE_USERS="hide_users";
	public static final String IGNORE_HIDE_USERS="ignore_hide_users";

}
