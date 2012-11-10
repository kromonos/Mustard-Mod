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

package org.mustard.geonames;

import java.util.TimeZone;

public class GeoName {

	private String adminCode1;
	private String adminCode2;
	private String adminName1;
	private String adminName2;
	private String adminName3;
	private String adminName4;
	private String countryCode;
	private String countryName;
	private String distance;
	private String elevation;
	private String fclName;
	private String fcl;
	private String fcodeName;
	private String fcode;
	private long geonameId;
	private String lat;
	private String lng;
	private String name;
	private String population;	
	private TimeZone timezone;
	
	public String getAdminCode1() {
		return adminCode1;
	}
	public void setAdminCode1(String adminCode1) {
		this.adminCode1 = adminCode1;
	}
	public String getAdminCode2() {
		return adminCode2;
	}
	public void setAdminCode2(String adminCode2) {
		this.adminCode2 = adminCode2;
	}
	public String getAdminName1() {
		return adminName1;
	}
	public void setAdminName1(String adminName1) {
		this.adminName1 = adminName1;
	}
	public String getAdminName2() {
		return adminName2;
	}
	public void setAdminName2(String adminName2) {
		this.adminName2 = adminName2;
	}
	public String getAdminName3() {
		return adminName3;
	}
	public void setAdminName3(String adminName3) {
		this.adminName3 = adminName3;
	}
	public String getAdminName4() {
		return adminName4;
	}
	public void setAdminName4(String adminName4) {
		this.adminName4 = adminName4;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getElevation() {
		return elevation;
	}
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	public String getFclName() {
		return fclName;
	}
	public void setFclName(String fclName) {
		this.fclName = fclName;
	}
	public String getFcl() {
		return fcl;
	}
	public void setFcl(String fcl) {
		this.fcl = fcl;
	}
	public String getFcodeName() {
		return fcodeName;
	}
	public void setFcodeName(String fcodeName) {
		this.fcodeName = fcodeName;
	}
	public String getFcode() {
		return fcode;
	}
	public void setFcode(String fcode) {
		this.fcode = fcode;
	}
	public long getGeonameId() {
		return geonameId;
	}
	public void setGeonameId(long geonameId) {
		this.geonameId = geonameId;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPopulation() {
		return population;
	}
	public void setPopulation(String population) {
		this.population = population;
	}
	public TimeZone getTimezone() {
		return timezone;
	}
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

	
}
