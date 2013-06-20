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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mumod.android.Account;
import org.mumod.android.MustardApplication;
import org.mumod.android.MustardDbAdapter;
import org.mumod.android.Preferences;
import org.mumod.rsd.Service;
import org.mumod.statusnet.DirectMessage;
import org.mumod.statusnet.Group;
import org.mumod.statusnet.Relationship;
import org.mumod.statusnet.Status;
import org.mumod.statusnet.StatusNetService;
import org.mumod.statusnet.User;
import org.mumod.util.AuthException;
import org.mumod.util.HttpManager;
import org.mumod.util.MustardException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.content.Context;
import android.util.Log;

public class StatusNet {
	
	protected static final String TAG="StatusNet";

	// RSD
	private final String RSD = "/rsd.xml";
	
	// STATUSNET API
	private final String API_STATUSNET_VERSION = "/statusnet/version.json";
	private final String API_STATUSNET_CONFIG = "/statusnet/config.json";
	private final String API_HELP_TEST = "/help/test.json";
	private final String API_SN_STRING = "/api";
	
	// TWITTER API
	private final String API_TWITTER_STRING = "/1.1";

	// GLOBAL API
	private final String API_PUBLIC_TIMELINE = "/statuses/public_timeline.json";
	private final String API_STATUS_SHOW = "/statuses/show/%s.json";
	
	// USER API
	private final String API_USER_CHECK =  "/account/verify_credentials.json";
	private final String API_USER_SHOW = "/users/show.json?screen_name=%s";
	private final String API_USER_TIMELINE = "/statuses/user_timeline.json?screen_name=%s";
	private final String API_USER_MENTIONS = "/statuses/mentions.json?screen_name=%s";

	private final String API_USER_FAVORITES = "/favorites.json?screen_name=%s";
	
	private final String API_USER_FRIENDS_TIMELINE = "/statuses/friends_timeline.json?screen_name=%s";
	private final String API_USER_FRIENDS_TIMELINE_TWITTER = "/statuses/home_timeline.json?";
	private final String API_USER_SUBSCRIBE = "/friendships/create.json?screen_name=%s";
	private final String API_USER_UNSUBSCRIBE = "/friendships/destroy.json?screen_name=%s";
	
	private final String API_FRIENDSHIP_SHOW = "/api/friendships/show.json?target_screen_name=%s";
	private final String API_FRIENDSHIP_SHOW_TWITTER = "/friendships/show.json?target_screen_name=%t&source_screen_name=%s";
		
	private final String API_USER_BLOCK = "/blocks/create/%s.json";
	private final String API_USER_UNBLOCK = "/blocks/destroy/%s.json";
	private final String API_USER_AVATAR = "/account/update_profile_image.json";
	
	// DIRECT MESSAGES
	private final String API_DM_IN = "/direct_messages.json";
	private final String API_DM_OUT = "/direct_messages/sent.json";
	private final String API_DM_ADD = "/direct_messages/new.json";
	
	// NOTICE API
	private final String API_NOTICE_ADD = "/statuses/update.json";
	private final String API_NOTICE_DELETE = "/statuses/destroy/%s.json";
	private final String API_NOTICE_FAVOR = "/favorites/create/%s.json";
	private final String API_NOTICE_DISFAVOR = "/favorites/destroy/%s.json";
	private final String API_NOTICE_REPEAT = "/statuses/retweet/%s.json";

	// GROUP API
	private final String API_GROUP_SHOW = "/statusnet/groups/show/%s.json";
	private final String API_GROUP_TIMELINE = "/statusnet/groups/timeline/%s.json";
	private final String API_GROUP_JOIN = "/statusnet/groups/join/%s.json";
	private final String API_GROUP_LEAVE = "/statusnet/groups/leave/%s.json";
	private final String API_GROUP_IS_MEMBER = "/statusnet/groups/is_member.json?group_name=%s";
	
	// TAG API
	private final String API_TAG_TIMELINE = "/statusnet/tags/timeline/%s.json";

	// SEARCH API
	private final String API_SEARCH = "/search.json";

	private HttpManager mHttpManager;
	private URL mURL;
	private String mUsername;
	private long mUserId;
	private long mUsernameId;
	private int mMaxNotices = Preferences.FETCH_MAX_ITEMS;
	private Context mContext;
	
	private Account mAccount;
	
	public void isTwitter() {
		if(mURL.toExternalForm().endsWith("twitter.com")) {
			isTwitter = true;
		}
		else {
			isTwitter = false;
		}			
	}
	
	public Account getAccount() {
		return mAccount;
	}

	public void setAccount(Account account) {
		this.mAccount = account;
	}

	private boolean isTwitter = false;
	
	public StatusNet(Context context) {
		mContext=context;
	}

	public void setMaxNotices(int maxNotices) {
		mMaxNotices=maxNotices;
	}
	
	public void setURL(URL url) {
		mURL = url;
		String host = url.getHost();
		if(host.equalsIgnoreCase("twitter.com")) {
			host="api.twitter.com";
			try {
				mURL = new URL("https://"+host+"/1.1");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isTwitter = true;
		}
		mHttpManager = new HttpManager(mContext,host);
	}

	public URL getURL() {
		return mURL;
	}

	public void setCredentials(String username, String password) throws MustardException {
		if (mHttpManager == null) {
			throw new MustardException("You must call setURL prior"); 
		}
		mHttpManager.setCredentials(username, password);
		this.mUsername=username;
		return ;
	}

	public void setCredentials(CommonsHttpOAuthConsumer consumer,String username) throws MustardException {
		if (mHttpManager == null) {
			throw new MustardException("You must call setURL prior"); 
		}
		mHttpManager.setOAuthConsumer(consumer);
		this.mUsername=username;
	}

	public void setUserId(long user_id) {
		this.mUserId=user_id;
	}
	
	public long getUserId() {
		return mUserId;
	}
	
	public void setUsernameId(long username_id) {
		this.mUsernameId=username_id;
	}
	
	public long getUsernameId() {
		return mUsernameId;
	} 
	public String getMUsername() {
		return mUsername;
	}

	public User getUser(String username) throws MustardException {
		if(username==null || "".equals(username))
			throw new MustardException("Username is null");
		isTwitter();
		
		User user = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_SHOW.replace("%s", username.toLowerCase());
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
			} catch (JSONException e) {
				if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}
	
	private String getTagValueWithMultiItem(Element eElement){
		String returnVal = "" ;
		Node eNode ;
		int NumOFItem = eElement.getElementsByTagName("link").getLength();
		for (int y = 0; y < NumOFItem; y++) {
			eNode = eElement.getElementsByTagName("link").item(y);
			NamedNodeMap attributes = eNode.getAttributes();
			for (int g = 0; g < attributes.getLength(); g++) {
				Attr attribute = (Attr)attributes.item(g);
				if(attribute.getNodeName().equals("rel")&&attribute.getNodeValue().equals("alternate"))
				{
					try {
						if(eNode.getAttributes().getNamedItem("type").getNodeValue().equals("application/atom+xml"))
							returnVal =eNode.getAttributes().getNamedItem("href").getNodeValue();
					}  
					catch (Exception e) {
						if(MustardApplication.DEBUG)
							e.printStackTrace();
					}
				} 
			}
		}
		return returnVal;    
	}
	
	
	public User getRemoteUser(String profileUrl) throws MustardException {
		if(profileUrl==null || "".equals(profileUrl))
			throw new MustardException("Username is null");
		User user = null;
		JSONObject json = null;
		String userURL = "";
		try {
			// I need a new HttpManager.. I don't have to send Auth
			URL _url = new URL(profileUrl);
			String host = _url.getHost();
			HttpManager _hm = new HttpManager(mContext,host);
			Document d = _hm.getDocument(profileUrl, HttpManager.GET, null);
			userURL = getTagValueWithMultiItem(d.getDocumentElement());
			Log.d("Mustard","Found: " + userURL);
			if(!userURL.equals("")) {
				String baseURL=userURL.substring(0, userURL.indexOf("statuses/user_timeline"));
				baseURL +="users/show.json?id=";
				String userid = userURL.substring(userURL.lastIndexOf("/")+1, userURL.lastIndexOf(".atom"));
				baseURL +=userid;
				Log.d("Mustard","Proviamo con " + baseURL);
				json = _hm.getJsonObject(baseURL, HttpManager.GET);
			}
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
//			if (MustardApplication.DEBUG) 
				e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
				user.setProfile_json_url(userURL);
			} catch (JSONException e) {
				if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}

	public Group getGroup(String groupname) throws MustardException {
		Group group = null;
		JSONObject json = null;
		isTwitter();
		try {
			String userURL = mURL.toExternalForm()
					+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_GROUP_SHOW.replace("%s", groupname.toLowerCase());
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				group = StatusNetJSONUtil.getGroup(json);
			} catch (JSONException e) {
				throw new MustardException(e.toString());
			}
		}
		return group;
	}
	
	public ArrayList<Status> getSearch(String str)  throws MustardException {
		String q="";
		try {
			q=URLEncoder.encode(str, HTTP.UTF_8);
		} catch (Exception e) {
			throw new MustardException(e.toString());
		}
		String lURL = "";
		if(isTwitter)
			lURL = "http://search.twitter.com/" + API_SEARCH + "?q="+q+ "&rpp="+ mMaxNotices + "&result_type=recent";
		else {
			lURL = mURL.toExternalForm() + "/api" + API_SEARCH + "?q="+q+ "&count="+ mMaxNotices ;
		}
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONObject bo = mHttpManager.getJsonObject(lURL);
			//System.out.println(bo.toString());
			JSONArray aj = bo.getJSONArray("results");
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatusFromSearch(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (MustardApplication.DEBUG) Log.d(TAG,"Found " + aj.length() + " dents");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}

	public ArrayList<Status> getRemote(int type, String extra,long sinceId,boolean higher) throws MustardException {
		switch (type) {
		case MustardDbAdapter.ROWTYPE_USER:
			return getRemoteUserTimeline(extra,sinceId,higher);

		}
		return null;
	}
	
	public ArrayList<Status> get(int type, String extra,long sinceId,boolean higher) throws MustardException {
		switch (type) {
		case MustardDbAdapter.ROWTYPE_FRIENDS:
			return getFriendsTimeline(extra,sinceId,higher);

		case MustardDbAdapter.ROWTYPE_PUBLIC:
			return getPublicTimeline(sinceId,higher);

		case MustardDbAdapter.ROWTYPE_MENTION:
			return getMentions(extra,sinceId,higher);

		case MustardDbAdapter.ROWTYPE_USER:
			return getUserTimeline(extra,sinceId,higher);
		
		case MustardDbAdapter.ROWTYPE_GROUP:
			return getGroupTimeline(extra,sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_TAG:
			return getTagTimeline(extra,sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_SINGLE:
			return getStatus(extra);
			
		case MustardDbAdapter.ROWTYPE_SEARCH:
			return getSearch(extra);
			
		case MustardDbAdapter.ROWTYPE_FAVORITES:
			return getFavorites(extra, sinceId,higher);
			
		case MustardDbAdapter.ROWTYPE_CONVERSATION:
			return getConversation(extra);
		}
		return null;
	}
	
	private String buildParams(long limit,boolean higher) {
		isTwitter();
		if(limit<=0)
			return  isTwitter ? "&include_rts=true" : "";
		String sideVersus = "";
		if(higher)
			sideVersus="since_id";
		else
			sideVersus="max_id";
		return "&"+sideVersus+"="+limit  + (isTwitter ? "&include_rts=true" : "");
	}
	
	public ArrayList<Status> getPublicTimeline(long sinceId, boolean since) throws MustardException {
		isTwitter();
		String sinceParam = buildParams(sinceId,since);
		String lURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_PUBLIC_TIMELINE + "?count="
				+ mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getCurrentUserTimeline(long limit,boolean higher) throws MustardException {
		return getUserTimeline(mUsername,limit,higher);
	}
	
	public ArrayList<Status> getRemoteUserTimeline(String url,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		String lURL = url.replace(".atom", ".json") + "?count=" + mMaxNotices + sinceParam;
		return getGeneralRemoteStatuses(lURL);
	}
	public ArrayList<Status> getUserTimeline(String username,long limit,boolean higher) throws MustardException {
		isTwitter();
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_TIMELINE.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}
	
	public ArrayList<Status> getGroupTimeline(String groupname,long limit,boolean higher) throws MustardException {
		isTwitter();
		String sinceParam = buildParams(limit, higher);
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_GROUP_TIMELINE.replace("%s", groupname.toLowerCase())
				+ "?count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getCurrentUserFriendsTimeline(long limit,boolean higher) throws MustardException {
		return getFriendsTimeline(mUsername,limit,higher);
	}
	
	public ArrayList<Status> getFriendsTimeline(String username,long limit,boolean higher) throws MustardException {
		isTwitter();
		String sinceParam = buildParams(limit, higher);
		String lURL = "";
		if(isTwitter) {
			lURL = mURL.toExternalForm() + API_TWITTER_STRING + API_USER_FRIENDS_TIMELINE_TWITTER + "count=" + mMaxNotices + sinceParam;
		} else {
			lURL = mURL.toExternalForm()
				+ API_SN_STRING + API_USER_FRIENDS_TIMELINE.replace("%s", username
						.toLowerCase()) + "&count=" + mMaxNotices + sinceParam;
		}
		return getGeneralStatuses(lURL);
	}
	
	public ArrayList<Status> getMentions(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		isTwitter();
		if(username.equals("-1"))
			username=getMUsername();
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_MENTIONS.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}
	
	public ArrayList<DirectMessage> getDirectMessages(long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		isTwitter();
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_DM_IN + "?count=" + mMaxNotices + sinceParam;
		return getGeneralDirectMessages(DirectMessage.K_IN,lURL);
	}
	
	public ArrayList<DirectMessage> getDirectMessagesSent(long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		isTwitter();
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_DM_OUT + "?count=" + mMaxNotices + sinceParam;
		return getGeneralDirectMessages(DirectMessage.K_OUT,lURL);
	}
	
	public ArrayList<Status> getFavorites(String username,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		isTwitter();
		String lURL = mURL.toExternalForm()
				+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_FAVORITES.replace("%s", username.toLowerCase())
				+ "&count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public ArrayList<Status> getTagTimeline(String tag,long limit,boolean higher) throws MustardException {
		String sinceParam = buildParams(limit, higher);
		isTwitter();
		String lURL = mURL.toExternalForm()
		+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_TAG_TIMELINE.replace("%s", tag.toLowerCase())
		+ "?count=" + mMaxNotices + sinceParam;
		return getGeneralStatuses(lURL);
	}

	public Status getStatus(String id,boolean single) throws MustardException {
		Status s = null;
		isTwitter();
		String lURL = mURL.toExternalForm()
		+ (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_STATUS_SHOW.replace("%s", id);
		try {
			JSONObject o = mHttpManager.getJsonObject(lURL);
			s = StatusNetJSONUtil.getStatus(o);
		} catch (Exception e) {
			if (MustardApplication.DEBUG)
				e.printStackTrace();
		}
		return s;
	}

	public ArrayList<Status> getConversation (String id) throws MustardException {
		ArrayList<Status> tmp = new ArrayList<Status>();
		
		Status s = getStatus(id,true);
		tmp.add(s);
		while(true) {
			try {
				long prev = s.getNotice().getIn_reply_to_status_id();
				if (prev > 0) {
					s = getStatus(Long.toString(prev),true);
					tmp.add(s);
				} else {
					break;
				}
			} catch (Exception e) {
				if(MustardApplication.DEBUG)
					e.printStackTrace();
				break;
			}
		}
		return tmp;
	}
	
	public ArrayList<Status> getStatus(String id) throws MustardException {
		ArrayList<Status> ret = new ArrayList<Status>(1);
		ret.add(getStatus(id,true));
		return ret;
	}

	private ArrayList<Status> getGeneralRemoteStatuses(String url) throws MustardException {
		if(MustardApplication.DEBUG)
			Log.i("Mustard",url);
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			URL u = new URL(url);
			HttpManager _hm = new HttpManager(mContext,u.getHost());
			JSONArray aj = _hm.getJsonArray(url);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	private ArrayList<Status> getGeneralStatuses(String url) throws MustardException {
		if(MustardApplication.DEBUG)
			Log.i("Mustard",url);
		ArrayList<Status> statuses = new ArrayList<Status>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getStatus(o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	
	private ArrayList<DirectMessage> getGeneralDirectMessages(int inOut, String url) throws MustardException {
//		if(MustardApplication.DEBUG)
			Log.i("Mustard",url);
		ArrayList<DirectMessage> statuses = new ArrayList<DirectMessage>();
		try {
			JSONArray aj = mHttpManager.getJsonArray(url,HttpManager.GET,null,false);
			for (int i = 0; i < aj.length(); i++) {
				try {
					JSONObject o = aj.getJSONObject(i);
					statuses.add(StatusNetJSONUtil.getDirectMessage(inOut,o));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (MustardException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (AuthException e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MustardException(e.toString());
		}
		return statuses;
	}
	
	
	public User checkUser() throws MustardException {
		isTwitter();
		Log.d("checkUser", "mURL: " + mURL + " -- UserCheckURL: " + mURL.toExternalForm() + (!isTwitter ? "/api" : "") + API_USER_CHECK);
		User user = null;
		JSONObject json = null;
		try {
			String userURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_CHECK;
			json = mHttpManager.getJsonObject(userURL, HttpManager.GET);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) {
			if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		if (json != null) {
			try {
				user = StatusNetJSONUtil.getUser(json);
			} catch (JSONException e) {
				if (MustardApplication.DEBUG) e.printStackTrace();
				throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
			}
		}
		return user;
	}
	
	public boolean delete(String id) {
		isTwitter();
		String deleteURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_NOTICE_DELETE.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(deleteURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}

	public boolean doFavour(String id) {	
		isTwitter();
		String favorURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_NOTICE_FAVOR.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(favorURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
		
	public boolean doDisfavour(String id) {	
		isTwitter();
		String disfavorURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_NOTICE_DISFAVOR.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(disfavorURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doRepeat(String id) {
		isTwitter();
		String favorURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_NOTICE_REPEAT.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(favorURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doSubscribe(String id)  throws MustardException {
		isTwitter();
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_SUBSCRIBE.replace("%s", id);
		try {
			if (MustardApplication.DEBUG)Log.v(TAG, subscribeURL);
			mHttpManager.getResponseAsString(subscribeURL, HttpManager.POST);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) { 
//			e.printStackTrace();
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}
	
	public boolean doUnsubscribe(String id) throws MustardException {	
		isTwitter();
		String unsubscribeURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_UNSUBSCRIBE.replace("%s", id);
		try {
			if (MustardApplication.DEBUG) Log.v(TAG, unsubscribeURL);
			mHttpManager.getResponseAsString(unsubscribeURL, HttpManager.POST);
		} catch (MustardException e) {
			throw e;
		} catch (Exception e) { 
//			e.printStackTrace();
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			throw new MustardException(e.getMessage() == null ? e.toString() : e.getMessage());
		}
		return true;
	}
	
	public boolean doBlock(String id) {	
		isTwitter();
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_BLOCK.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(subscribeURL, HttpManager.POST);
//			Log.d(TAG, "BLOCK: " + ret);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG)
				Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doUnblock(String id) {	
		isTwitter();
		String unsubscribeURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_UNBLOCK.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(unsubscribeURL, HttpManager.POST);
//			Log.d(TAG, "UNBLOCK: " + ret);
		} catch (Exception e) {
//			if (MustardApplication.DEBUG)
				Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doJoinGroup(String id) {	
		isTwitter();
		String joinGroupURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_GROUP_JOIN.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(joinGroupURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public boolean doLeaveGroup(String id) {	
		isTwitter();
		String leaveGroupURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_GROUP_LEAVE.replace("%s", id);
		try {
			mHttpManager.getResponseAsString(leaveGroupURL, HttpManager.POST);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
		return true;
	}
	
	public Relationship getFriendshipStatus(String user) {
		isTwitter();
		Relationship r = null;
		String friendshipExistsURL = mURL.toExternalForm() +
			API_FRIENDSHIP_SHOW.replace("%s", user);
		if(isTwitter)
			friendshipExistsURL = mURL.toExternalForm() +
			API_FRIENDSHIP_SHOW_TWITTER.replace("%t", user).replace("%s", mUsername);
		try {
			JSONObject friendship = mHttpManager.getJsonObject(friendshipExistsURL, HttpManager.GET);
			r = StatusNetJSONUtil.getRelationship(friendship);
		} catch (Exception e) {
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			e.printStackTrace();
		}
		return r;
	}
	
	public boolean isGroupMember(String group) {
		isTwitter();
		String subscribeURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_GROUP_IS_MEMBER.replace("%s", group);
		try {
			JSONObject isMember = mHttpManager.getJsonObject(subscribeURL, HttpManager.POST);
			return isMember.getBoolean("is_member");
		} catch (Exception e) {
			e.printStackTrace();
			if (MustardApplication.DEBUG) Log.e(TAG,e.toString());
			return false;
		}
	}
	
	public String getVersion() {
		isTwitter();
		String version = null;
		try {
			String versionURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_STATUSNET_VERSION;
			version = mHttpManager.getResponseAsString(versionURL, HttpManager.GET);
			if (version!=null) {
				version = version.trim();
				if(version.startsWith("\""))
					version=version.replaceAll("\"", "");
			}
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
		
	}
	
	public Service getRsd() {

		Service rsd = null;
		InputStream is = null;
		try {
			String rsdURL = mURL.toExternalForm() + RSD;
			is = mHttpManager.requestData(rsdURL, HttpManager.GET,null);
			
			rsd = StatusNetXMLUtil.getRsd(is);
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
		}
		return rsd;
		
	}
	
	public boolean isHelpTest() {
		isTwitter();

		boolean pass = false;
		try {
			String helptestURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_HELP_TEST;
			String helpTest = mHttpManager.getResponseAsString(helptestURL, HttpManager.GET);
			if (helpTest!=null) {
				helpTest = helpTest.trim();
				if(helpTest.startsWith("\""))
					helpTest=helpTest.replaceAll("\"", "");
				if (helpTest.equalsIgnoreCase("ok"))
					pass = true;
			}
		} catch (MustardException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pass;
		
	}
	
	public long sendDirectMessage(String text, String screen_name) throws MustardException, AuthException {
		isTwitter();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("text", text));
	    params.add(new BasicNameValuePair("screen_name", screen_name));
	    params.add(new BasicNameValuePair("source", MustardApplication.APPLICATION_NAME));
	    String updateURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_DM_ADD;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	String res = mHttpManager.getResponseAsString(updateURL, HttpManager.POST, params);
	    	json = new JSONObject(res);
	    	id = json.getLong("id");
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	if(MustardApplication.DEBUG) 
	    		e.printStackTrace();
	    	throw e;
	    } catch (Exception e) {
	    	if(MustardApplication.DEBUG) 
	    		e.printStackTrace();
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}

	public StatusNetService getConfiguration() throws MustardException {
		isTwitter();

		StatusNetService config = new StatusNetService();
		JSONObject o = null;
		try {
			String configURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_STATUSNET_CONFIG;
			o = mHttpManager.getJsonObject(configURL);
			// System.out.println(o.toString(3));
			config = StatusNetJSONUtil.getService(o);
		} catch (MustardException e) {
			throw e;
		} catch (AuthException e) {
			// Impossible here :P
			throw new MustardException(e.getMessage());
		} catch (IOException e) {
			throw new MustardException(e.getMessage());
		} catch (JSONException e) {
			throw new MustardException(e.getMessage());
		}
		return config;
	}
	
	public long update(String status, String in_reply_to) throws MustardException, AuthException {
		return update(status, in_reply_to, null, null, null);
	}

	public long update(String status, String in_reply_to,String lon, String lat) throws MustardException, AuthException {
		return update(status, in_reply_to, lon, lat, null);
	}
	
	public long update(String status, String in_reply_to, String lon, String lat, File media) throws MustardException, AuthException {
		isTwitter();
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("status", status));
	    params.add(new BasicNameValuePair("source", MustardApplication.APPLICATION_NAME));
	    if(!in_reply_to.equals("") && !in_reply_to.equals("-1"))
	    	params.add(new BasicNameValuePair("in_reply_to_status_id", in_reply_to));
	    if(lon!=null && !"".equals(lon))
	    	params.add(new BasicNameValuePair("long", lon));
	    if(lat!=null && !"".equals(lat))
	    	params.add(new BasicNameValuePair("lat", lat));

	    String updateURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_NOTICE_ADD;
	    JSONObject json = null;
	    long id=-1;
	    try {
	    	if (media == null) {
//	    		String yfrogurl = "";
//	    		if(isTwitter) {
//	    			yfrogurl = Yfrog.test(status,media);
//	    			Log.i(TAG, "Yfrog response: " + yfrogurl);
//	    		}
	    		json = mHttpManager.getJsonObject(updateURL, HttpManager.POST, params);
	    	} else {
	    		if(!isTwitter)
	    			json = mHttpManager.getJsonObject(updateURL, params,"media",media);

	    	}
//	    	System.out.println(json.toString(1));
	    	Status s = StatusNetJSONUtil.getStatus(json);
	    	id = s.getNotice().getId();
	    } catch (AuthException e) {
	    	throw e;
	    } catch (MustardException e) {
	    	if(MustardApplication.DEBUG) 
	    		e.printStackTrace();
	    	throw e;
	    } catch (Exception e) {
	    	if(MustardApplication.DEBUG) 
	    		e.printStackTrace();
	    	throw new MustardException(e.getMessage());
	    }
		return id;
	}
	
	public boolean updateAvatar(File media) throws MustardException {
		isTwitter();
		String updateURL = mURL.toExternalForm() + (!isTwitter ? API_SN_STRING : API_TWITTER_STRING) + API_USER_AVATAR;
		try {
			mHttpManager.getJsonObject(updateURL,null,"image",media);
			return true;
		} catch (Exception e) {
			if (MustardApplication.DEBUG) e.printStackTrace();
			throw new MustardException(e.getMessage());
		}
	}
	
	public boolean isTwitterInstance() {
		return isTwitter;
	}
}
