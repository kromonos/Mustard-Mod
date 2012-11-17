package org.mumod.android;

public class Account {

	private long id;
	private String username;
	private String instance;
	private String version;
	private int textLimit;
	private long attachlimit;
	
	public long getAttachlimit() {
		return attachlimit;
	}
	public void setAttachlimit(long attachlimit) {
		this.attachlimit = attachlimit;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getTextLimit() {
		return textLimit;
	}
	public void setTextLimit(int textLimit) {
		this.textLimit = textLimit;
	}
	
}
