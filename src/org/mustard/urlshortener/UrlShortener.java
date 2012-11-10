package org.mustard.urlshortener;

import java.util.HashMap;

import org.mustard.util.MustardException;

public interface UrlShortener {

	public String getShorterName() ;
	
	public String doShort(String longUrl, HashMap<String, String> params) throws MustardException;
	
	public String doShort(String longUrl) throws MustardException;
	
}
