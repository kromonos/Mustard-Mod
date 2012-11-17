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

package org.mumod.android.activity;

import org.mumod.android.MustardApplication;
import org.mumod.android.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AboutDialog {

	static void show(Context context) {

		
		View view = LayoutInflater.from(context).inflate(R.layout.about, null);
		TextView tv = (TextView)view.findViewById(R.id.tv_about);
		tv.setText(context.getString(R.string.about_message,MustardApplication.sVersionName));


		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//builder.setInverseBackgroundForced(true);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setView(view);
		builder.setCancelable(true);
		builder.setTitle(R.string.about_title);
		builder.setPositiveButton(R.string.btn_squeeze, null);
		builder.create().show();
	}

}
