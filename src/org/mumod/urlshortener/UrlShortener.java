package org.mumod.urlshortener;

import java.util.HashMap;

import org.mumod.util.MustardException;

public interface UrlShortener {

	public String getShorterName() ;
	
	public String doShort(String longUrl, HashMap<String, String> params) throws MustardException;
	
	public String doShort(String longUrl, String urlStr, String yourlsAPIKey) throws MustardException;
	
}
