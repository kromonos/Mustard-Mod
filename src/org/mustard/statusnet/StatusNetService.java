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

public class StatusNetService {
	
	public Site site;
	public License license;
	
	public StatusNetService() {
		site = new Site();
		license = new License();
	}
	
	public class Site {
		
		public String name;
		public String server;
		
		public String theme;
		public String path;
		public boolean fancy;
		public String language;
		public String email;
		public String broughtby;
		public String broughtbyurl;
		public String closed;
		public int textlimit;
		public boolean inviteonly;
		public boolean privatesite;
		public boolean ssl;
		public String sslserver;
		public int shorturllength;

		public boolean attachment;
		public long attachmentMaxSize;
		
	}

	public class License {
		
		public String url;
		public String title;
		public String image;
		
	}
	
	
}
