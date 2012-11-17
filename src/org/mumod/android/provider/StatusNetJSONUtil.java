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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mumod.android.MustardApplication;
import org.mumod.statusnet.Attachment;
import org.mumod.statusnet.DirectMessage;
import org.mumod.statusnet.Group;
import org.mumod.statusnet.Notice;
import org.mumod.statusnet.Relationship;
import org.mumod.statusnet.Status;
import org.mumod.statusnet.StatusNetService;
import org.mumod.statusnet.User;

import android.util.Log;

public class StatusNetJSONUtil {
	
	private static final String TAG = "Mustard";
	
	private static DateFormat df =  new SimpleDateFormat("E MMM d HH:mm:ss Z yyyy",Locale.ENGLISH);
	private static DateFormat dfs =  new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z",Locale.ENGLISH);
	
	public static Status getStatus(JSONObject json) throws JSONException {
		Status status = new Status();
		status.setNotice(getNotice(json));
		status.setUser(getUser(json.getJSONObject("user")));
		return status;
	}

	public static User getUser(JSONObject json) throws JSONException {
		
		User user = new User();
		user.setId(json.getLong("id"));
		user.setDescription(json.getString("description"));
		user.setScreen_name(json.getString("screen_name"));
		user.setName(json.getString("name"));
		user.setLocation(json.getString("location"));
		user.setProfile_image_url(json.getString("profile_image_url"));
		user.setUrl(json.getString("url"));
		user.setStatuses_count(json.getInt("statuses_count"));
		user.setFavourites_count(json.getInt("favourites_count"));
		user.setFollowers_count(json.getInt("followers_count"));
		user.setFriends_count(json.getInt("friends_count"));
		user.setTime_zone(json.getString("time_zone"));
		try {
			user.setUtc_offset(json.getInt("utc_offset"));
		}  catch (JSONException e) {
			user.setUtc_offset(0);
		}
		boolean following = false;
		try {
			following=json.getBoolean("following");
		} catch (JSONException e) {
		}
		user.setFollowing(following);
		boolean blocking = false;
		try {
			blocking = json.getBoolean("statusnet_blocking");
		} catch (JSONException e) {
		}
		user.setBlocking(blocking);
		String profile_url = "";
		try {
			profile_url = json.getString("statusnet_profile_url");
		} catch (JSONException e) {
		}
		user.setProfile_url(profile_url);
		try {
			user.setCreated_at(df.parse(json.getString("created_at")));
		} catch (ParseException e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
		}
		try {
			JSONObject jstatus = json.getJSONObject("status");
			user.setStatus(getNotice(jstatus));
		} catch (JSONException e ) {
		}
		return user;
	}

	public static Notice getNotice(JSONObject json) throws JSONException {
		Notice notice = new Notice();
		notice.setId(json.getLong("id"));
		notice.setText(json.getString("text"));
		String statusnet_text = "";
		try {
			statusnet_text=json.getString("statusnet_html");
		} catch(JSONException e) {
		}
		notice.setStatusnet_text(statusnet_text);
		notice.setTruncated(json.getBoolean("truncated"));
		try {
			notice.setCreated_at(df.parse(json.getString("created_at")));
		} catch (ParseException e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
		}
		notice.setSource(json.getString("source"));
		notice.setIn_reply_to_screen_name(json.getString("in_reply_to_screen_name"));
		
		try {
			notice.setIn_reply_to_user_id(json.getLong("in_reply_to_user_id"));
			notice.setIn_reply_to_status_id(json.getLong("in_reply_to_status_id"));
		} catch (JSONException e) {
		}
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		try {
			
			JSONArray jattachments = json.getJSONArray("attachments");
			if (jattachments != null) {
				for (int i=0;i<jattachments.length();i++) {
					JSONObject enclosure = jattachments.getJSONObject(i);
					Attachment a = new Attachment();
					a.setMimeType(enclosure.getString("mimetype"));
					a.setSize(enclosure.getLong("size"));
					a.setUrl(enclosure.getString("url"));
					attachments.add(a);
					//if (MustardApplication.DEBUG) Log.d(TAG,"Found attachment: " + a.getUrl());
				}
				notice.setAttachments(attachments);
			}
		} catch (JSONException e ) {
		}
		if (json.has("geo")) {
			try {
				JSONObject geo = json.getJSONObject("geo");
				JSONArray coordinates = null;
				if (geo.has("coordinates")) {
					try {
						coordinates = geo.getJSONArray("coordinates");
						notice.setGeo(true);
						notice.setLat(coordinates.getDouble(0));
						notice.setLon(coordinates.getDouble(1));
					} catch (JSONException e) {
						e.printStackTrace();
						notice.setGeo(false);
					}
				} else {
					Log.d(TAG,"Geo tag empty");
					notice.setGeo(false);
				}
			} catch (JSONException e) {
				notice.setGeo(false);
			}
		} else {
			Log.d(TAG,"No geo tag");
			notice.setGeo(false);
		}
		notice.setFavorited(json.getBoolean("favorited"));
		return notice;
	}

	public static Status getStatusFromSearch(JSONObject json) throws JSONException {
		Status status = new Status();
		status.setNotice(getNoticeFromSearch(json));
		status.setUser(getUserFromSearch(json));
		return status;
	}

	public static User getUserFromSearch(JSONObject json) throws JSONException {
		User user = new User();
		user.setId(json.getInt("from_user_id"));
		user.setProfile_image_url(json.getString("profile_image_url"));
		user.setScreen_name(json.getString("from_user"));
		user.setDescription("");
		user.setName("");
		return user;
	}

	public static Notice getNoticeFromSearch(JSONObject json) throws JSONException {
		Notice notice = new Notice();
		notice.setId(json.getLong("id"));
		notice.setText(json.getString("text"));
		try {
			notice.setCreated_at(dfs.parse(json.getString("created_at")));
		} catch (ParseException e) {
			if (MustardApplication.DEBUG) Log.e(TAG,"Parsing created_at: " + e.toString());
		}
		notice.setSource(json.getString("source"));
		try {
			notice.setIn_reply_to_screen_name(json.getString("to_user"));
		} catch (JSONException e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
		}
		try {
			notice.setIn_reply_to_user_id(json.getLong("to_user_id"));
		} catch (JSONException e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
		}
		return notice;
	}

	public static Group getGroup(JSONObject json) throws JSONException {
		Group group = new Group();
		try {
			group.setId(json.getLong("id"));
			group.setUrl(json.getString("url"));
			group.setNickname(json.getString("nickname"));
			group.setFullname(json.getString("fullname"));
//			group.setHomepage_url(json.getString("homepage_url"));
			group.setOriginal_logo(json.getString("original_logo"));
			group.setHomepage_logo(json.getString("homepage_logo"));
			group.setStream_logo(json.getString("stream_logo"));
			group.setMini_logo(json.getString("mini_logo"));
			group.setHomepage(json.getString("homepage"));
			group.setDescription(json.getString("description"));
			group.setLocation(json.getString("location"));

			try {
				group.setCreated(df.parse(json.getString("created")));
			} catch (ParseException e) {
				if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			}

			try {
				group.setModified(df.parse(json.getString("modified")));
			} catch (ParseException e) {
				if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			}
		} catch(Exception e) {
			throw new JSONException(e.getMessage());
		}
		return group;
	}
	
	public static StatusNetService getService(JSONObject json) throws JSONException {
		StatusNetService service = null;
		if (json.has("site")) {
			service = new StatusNetService();
			JSONObject jsite = json.getJSONObject("site");
			try {
				service.site.fancy = jsite.getBoolean("fancy");
			} catch (JSONException e) {
				try {
					service.site.fancy = jsite.getString("fancy").equals("1");
				} catch (Exception ee) {
					// SHIT
				}
			}
			try {
				service.site.privatesite = jsite.getBoolean("private");
			} catch (JSONException e) {
				try {
					service.site.privatesite = jsite.getString("private").equals("1");
				} catch (Exception ee) {
					// SHIT
				}
			}
			int textlimit=140;
			try {
				textlimit=jsite.getInt("textlimit");
			} catch (JSONException e) {
			}
			service.site.textlimit=textlimit;
			
			if(json.has("attachments")) {
				JSONObject jattach = json.getJSONObject("attachments");
				boolean attach_enabled = jattach.has("uploads") ? jattach.getBoolean("uploads") : false;
				service.site.attachment = attach_enabled;
				long attach_max_size = jattach.has("file_quota") ? jattach.getLong("file_quota") : 1048576;
				service.site.attachmentMaxSize = attach_max_size;
			}
		}
		
		return service;
	}
	
	public static Relationship getRelationship(JSONObject json) throws JSONException {
		
		Relationship r = new Relationship();
		Relationship.User source = r.getSource();
		Relationship.User target = r.getTarget();
		
		JSONObject relationship = json.getJSONObject("relationship");
		
		JSONObject jsource = relationship.getJSONObject("source");
		try {
			target.setBlocking(jsource.getBoolean("blocking"));
		} catch (JSONException e) {
			
		}
		source.setFollowed_by(jsource.getBoolean("followed_by"));
		boolean following = false;
		try {
			following=jsource.getBoolean("following");
		} catch (JSONException e) {
			
		}
		source.setFollowing(following);
		source.setScreen_name(jsource.getString("screen_name"));
		source.setId(jsource.getLong("id"));
		try {
			source.setNotifications_enabled(jsource.getBoolean("notifications_enabled"));
		} catch (JSONException e) {
			
		}
		JSONObject jtarget = relationship.getJSONObject("target");
		try {
			target.setBlocking(jtarget.getBoolean("blocking"));
		} catch (JSONException e) {
			
		}
		target.setFollowed_by(jtarget.getBoolean("followed_by"));
		try {
			following=jsource.getBoolean("following");
		} catch (JSONException e) {
			
		}
		target.setFollowing(following);
		target.setScreen_name(jtarget.getString("screen_name"));
		target.setId(jtarget.getLong("id"));
		try {
			target.setNotifications_enabled(jtarget.getBoolean("notifications_enabled"));
		} catch (JSONException e) {
			
		}
		return r;
	}

	public static DirectMessage getDirectMessage(int inOut,JSONObject json) throws JSONException {
		DirectMessage dm = new DirectMessage();
		dm.setId(json.getLong("id"));
		dm.setInOut(inOut);
		dm.setText(json.getString("text"));
		try {
			dm.setCreated_at(df.parse(json.getString("created_at")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject recipient = json.getJSONObject("recipient"); 
		dm.setRecipient_id(recipient.getLong("id"));
		dm.setRecipient_image(recipient.getString("profile_image_url"));
		dm.setRecipient_screenname(recipient.getString("screen_name"));
		
		JSONObject sender = json.getJSONObject("sender");
		dm.setSender_id(sender.getLong("id"));
		dm.setSender_image(sender.getString("profile_image_url"));
		dm.setSender_screenname(sender.getString("screen_name"));
		return dm;
	}
	
}
