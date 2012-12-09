package org.mumod.urlshortener;

import org.mumod.util.MustardException;

import android.content.Context;


public class Ur1ca extends LilUrlShortener {

	public Ur1ca(Context context) {
		super(context);
		lilUrl="http://ur1.ca";
	}

	public String getShorterName() {
		return "ur1.ca";
	}

}
