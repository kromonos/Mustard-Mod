package org.mumod.android.core;

@SuppressWarnings("serial")
public class FetchException extends Exception {

	private int code;

	public FetchException(String string) {
		super(string);
	}
	
	public FetchException(int code, String string) {
		super(string);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
