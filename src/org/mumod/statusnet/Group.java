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

package org.mumod.statusnet;

import java.util.Date;

public class Group {

	private long id;
	private String url;
	private String nickname;
	private String fullname;
//	private String homepage_url;
	private String original_logo;
	private String stream_logo;
	private String mini_logo;
	private String homepage;
	private String homepage_logo;
	private String description;
	private String location;
	private Date created;
	private Date modified;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
//	public String getHomepage_url() {
//		return homepage_url;
//	}
//	public void setHomepage_url(String homepage_url) {
//		this.homepage_url = homepage_url;
//	}
	public String getOriginal_logo() {
		return original_logo;
	}
	public void setOriginal_logo(String original_logo) {
		this.original_logo = original_logo;
	}
	public String getStream_logo() {
		return stream_logo;
	}
	public void setStream_logo(String stream_logo) {
		this.stream_logo = stream_logo;
	}
	public String getMini_logo() {
		return mini_logo;
	}
	public void setMini_logo(String mini_logo) {
		this.mini_logo = mini_logo;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getHomepage_logo() {
		return homepage_logo;
	}
	public void setHomepage_logo(String homepage_logo) {
		this.homepage_logo = homepage_logo;
	}

	
}
