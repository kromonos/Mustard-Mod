package org.mumod.statusnet;

public class RowStatus {

	private long id;
	private long statusId;
	private long accountId;
	private long userId;
	private String screenName;
	private String source;
	private long inReplyTo;
	private String inReplyToScreenName;
	private long repeatedId;
	private String repeatedByScreenName;
	private String profileImage;
	private String profileUrl;
	private long dateTime;
	private int geolocation;
	private String lon;
	private String lat;
	private int attachment;
	private String status;
	
	
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}
        public String getRepeatedByScreenName() {
                return repeatedByScreenName;
        }
        public void setRepeatedByScreenName(String repeatedByScreenName) {
                this.repeatedByScreenName = repeatedByScreenName;
        }
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(long inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
        public long getRepeatedId() {
                return repeatedId;
        }
        public void setRepeatedId(long repeatedId) {
                this.repeatedId = repeatedId;
        }
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public long getDateTime() {
		return dateTime;
	}
	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
	public int getGeolocation() {
		return geolocation;
	}
	public void setGeolocation(int geolocation) {
		this.geolocation = geolocation;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public int getAttachment() {
		return attachment;
	}
	public void setAttachment(int attachment) {
		this.attachment = attachment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}


}
