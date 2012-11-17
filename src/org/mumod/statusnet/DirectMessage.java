package org.mumod.statusnet;

import java.util.Date;

public class DirectMessage {
	
	public static final int K_IN  = 0;
	public static final int K_OUT = 1;
	
	private long id;
	private String text;
	private int inOut;
	private Date created_at;
	private long sender_id;
	private String sender_screenname;
	private String sender_image;
	private long recipient_id;
	private String recipient_screenname;
	private String recipient_image;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getInOut() {
		return inOut;
	}
	public void setInOut(int inOut) {
		this.inOut = inOut;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date createdAt) {
		created_at = createdAt;
	}
	public long getSender_id() {
		return sender_id;
	}
	public void setSender_id(long senderId) {
		sender_id = senderId;
	}
	public String getSender_screenname() {
		return sender_screenname;
	}
	public void setSender_screenname(String senderScreenname) {
		sender_screenname = senderScreenname;
	}
	public String getSender_image() {
		return sender_image;
	}
	public void setSender_image(String senderImage) {
		sender_image = senderImage;
	}
	public long getRecipient_id() {
		return recipient_id;
	}
	public void setRecipient_id(long recipientId) {
		recipient_id = recipientId;
	}
	public String getRecipient_screenname() {
		return recipient_screenname;
	}
	public void setRecipient_screenname(String recipientScreenname) {
		recipient_screenname = recipientScreenname;
	}
	public String getRecipient_image() {
		return recipient_image;
	}
	public void setRecipient_image(String recipientImage) {
		recipient_image = recipientImage;
	}
	public long getOtherId() {
		return inOut == K_IN ? sender_id : recipient_id;
	}
	public String getOtherScreenname() {
		return inOut == K_IN ? sender_screenname : recipient_screenname;
	}
	public String getOtherImage() {
		return inOut == K_IN ? sender_image : recipient_image;
	}
	
	public String toString() {
		return "Id: " + getId() + ", From: " + getSender_screenname() + ", To: " + getRecipient_screenname();
	}
}
