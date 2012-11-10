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

package org.mustard.statusnet;

public class Relationship {

	private User source;
	private User target;
	
	public Relationship() {
		source = new User();
		target = new User();
	}

	public User getSource() {
		return source;
	}

	public void setSource(User source) {
		this.source = source;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}	
	
	public class User {
		
		private String screen_name;
		private boolean followed_by;
		private boolean following;
		private boolean notifications_enabled;
		private boolean blocking;
		private long id;
		
		public String getScreen_name() {
			return screen_name;
		}
		
		public void setScreen_name(String screen_name) {
			this.screen_name = screen_name;
		}
		
		public boolean isFollowed_by() {
			return followed_by;
		}
		
		public void setFollowed_by(boolean followed_by) {
			this.followed_by = followed_by;
		}
		
		public boolean isFollowing() {
			return following;
		}
		
		public void setFollowing(boolean following) {
			this.following = following;
		}
		
		public boolean isNotifications_enabled() {
			return notifications_enabled;
		}
		
		public void setNotifications_enabled(boolean notifications_enabled) {
			this.notifications_enabled = notifications_enabled;
		}
		
		public boolean isBlocking() {
			return blocking;
		}
		
		public void setBlocking(boolean blocking) {
			this.blocking = blocking;
		}
		
		public long getId() {
			return id;
		}
		
		public void setId(long id) {
			this.id = id;
		}
		
	}

}
