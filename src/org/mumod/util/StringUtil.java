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

package org.mumod.util;

import android.util.Log;


public class StringUtil {
	
	private static String letters = "abcdefghijklmnopqrstuvwxyz";
	private static String numbers = "0123456789";
	
	private static String order = numbers + letters;
	
	private static boolean DEBUG = false;
	
	public static void main(String[] args) {
		if (args.length>2)
			DEBUG=true;
		System.out.println(args[0] + " " + args[1] + " = " +  compareVersion(args[0],args[1])  );
	}
	
	/**
	 * Check sign to see if versionA is major or minor of versionB
	 * return 0 indicate a unparsable version string 
	 * 
	 * @param versionA
	 * @param versionB
	 * @return
	 */
	public static int compareVersion(String versionA, String versionB) {
		if(versionA==null || versionB == null)
			return 0;
//		System.out.println("Checking " + versionA +" vs "+versionB);
		int ret=0;
		try {
			String[] aA = versionA.split("\\.");
			String[] aB = versionB.split("\\.");
			if (aA.length==0) {
				aA = new String[] {versionA};
			}
			if (aB.length==0) {
				aB = new String[] {versionB};
			}
			for (int i=0;i<aA.length;i++) {
				if (DEBUG)
					System.out.println("Checking " + aA[i]+" vs "+aB[i]);
				try {
					if (aA[i]==aB[i]) {
						if (DEBUG)
							System.out.println(aA[i]+" == "+aB[i]);
						continue;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					return 1;
				}
				int iA=-1;
				int iB=-1;
				boolean aN = false;
				boolean bN = false;
				try {
					iA=Integer.parseInt(aA[i]);
					aN=true;
				} catch(NumberFormatException e) {
					if (DEBUG)
						System.out.println(e.toString());
				}
				try {
					iB=Integer.parseInt(aB[i]);
					bN=true;
				} catch (NumberFormatException e) {
					if (DEBUG)
						System.out.println(e.toString());
				}
				if (aN && bN) {

					if (iA == iB) {
						if (DEBUG)
							System.out.println(iA +" == " + iB);
						continue;
					} else if (iA < iB) {
						if (DEBUG)
							System.out.println(iA +" < " + iB);
						return -1;
					} else {
						if (DEBUG)
							System.out.println(iA +" > " + iB);
						return 1;
					}
				} else {
					// Time to to the dirty work..
					if(aA[i].startsWith(aB[i])) {
						// Case:
						// 0a 0
						// 0abcd 0ab
						// 0a1 0a
						return 1;
					} else if ( aB[i].startsWith(aA[i])) {
						// Case:
						// 0 0a
						// 0ab 0abcd
						// 0a 0a1
						return -1;
					} else {
						// Case:
						// 0a 1b
						// 0ab 1b
						// 0a 1ab
						// 0ab 1ab
						// 10a 9av
						// A 0
						String nA="";
						for (int x=0;x<aA[i].length();x++) {
							if(numbers.indexOf(aA[i].charAt(x))>-1) {
								nA+=aA[i].charAt(x);
							} else {
								break;
							}
						}
						if (nA.equals("") && numbers.indexOf(aB[i])>-1) {
							// Case:
							// a 0a
							return -1;
						}
						String nB="";
						for (int x=0;x<aB[i].length();x++) {
							if(numbers.indexOf(aB[i].charAt(x))>-1) {
								nB+=aB[i].charAt(x);
							} else if (letters.indexOf(aB[i].charAt(x))>-1) {
								break;
							} else {
								// nothing
							}
						}
						if (nB.equals("")) {
							// Case:
							// 0 a
							return 1;
						}
						if (nA.equals(nB)) {
							// Case:
							// 9a1 9ba
							if (aA[i].length()<=aB[i].length()) {
								for (int x=0;x<aA[i].length();x++) {
									if (aA[i].charAt(x)==aB[i].charAt(x)) 
										continue;
									if (order.indexOf(aA[i].charAt(x)) > order.indexOf(aB[i].charAt(x))) {
										return 1;
									} else {
										return -1;
									}
								}
								return -1;
							} else {
								for (int x=0;x<aB[i].length();x++) {
									if (aA[i].charAt(x)==aB[i].charAt(x)) 
										continue;
									if (order.indexOf(aA[i].charAt(x)) > order.indexOf(aB[i].charAt(x))) {
										return 1;
									} else {
										return -1;
									}
								}
								return 1;
							}

						} else {
							// Case:
							// 8a 12b
							if (Integer.valueOf(nA) < Integer.valueOf(nB))
								return -1;
							else
								return 1;
						}
					}
				}
			}
			if (aA.length< aB.length)
				return -1;
		} catch (Exception e) {
			Log.e("Mustard",e.toString());
		}
		return ret;
	}


	
}
