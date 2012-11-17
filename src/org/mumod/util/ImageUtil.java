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

import java.io.File;

import org.mumod.android.MustardApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
public class ImageUtil {
	
	public static Bitmap resize(Bitmap bitmapOrg, int newWidth, int newHeight ) {

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight(); 

		// calculate the scale - in this case = 0.4f 
		float scaleWidth = ((float) newWidth) / width; 
		float scaleHeight = ((float) newHeight) / height; 

		// createa matrix for the manipulation 
		Matrix matrix = new Matrix(); 
		// resize the bit map 
		matrix.postScale(scaleWidth, scaleHeight); 
		// rotate the Bitmap 
//		matrix.postRotate(45);
		

		// recreate the new Bitmap 
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, 
				width, height, matrix, true);

		return resizedBitmap;
	}

	public static Bitmap crop(Bitmap bitmapOrg, int newWidth, int newHeight ) {

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight(); 
		int side = 0;
		int diff = 0;
		int xpos = 0;
		int ypos = 0;
		Bitmap bitmapCrop=null;
		if (width!=height) {
			if (width>height) {
				diff = width-height;
				side=height;
				xpos=Math.round(diff/2);
			} else {
				diff = height-width;
				side=width;
				ypos=Math.round(diff/2);
			}
			
			try {
				bitmapCrop = Bitmap.createBitmap(bitmapOrg, xpos, ypos, side, side);
			} catch (OutOfMemoryError e) {
				Log.e("ImageUtil","OutOfMemoryError when crop!!!!" + e.getMessage());
				return bitmapOrg;
			}
		} else {
			side = width;
			bitmapCrop=bitmapOrg;
		}

		if (side <= newWidth) {
			return bitmapCrop;
		}

		// calculate the scale - in this case = 0.4f 
		float scale = ((float) newWidth) / side; 
		// create a matrix for the manipulation 
		Matrix matrix = new Matrix(); 
		// resize the bit map 
		matrix.postScale(scale, scale); 
		
		try {
			bitmapOrg = Bitmap.createBitmap(bitmapCrop, 0, 0, side, side, matrix, true);
		} catch (OutOfMemoryError e) {
			Log.e("ImageUtil","OutOfMemoryError when resize!!!!" + e.getMessage());
			return bitmapCrop;
		} catch (Exception e) {
			if (MustardApplication.DEBUG)
				Log.d("ImageUtil",e.getMessage());
			return bitmapCrop;
		}
		return bitmapOrg;
	}
	
	public static Bitmap resize(File origin, long maxsize) {
		Bitmap bm = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
		int scale = 1;
		while ((opts.outWidth * opts.outHeight) * (1 / Math.pow(scale, 2)) > maxsize) {
			scale++;
		}

		if(scale>1) {
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = scale;
			Log.d("ImageUtil","decodeFile inSampleSize: " + scale);
			bm = BitmapFactory.decodeFile(origin.getAbsolutePath(),opts);
		} else {
			bm = BitmapFactory.decodeFile(origin.getAbsolutePath());
		}
		
		return bm;
	}
}
