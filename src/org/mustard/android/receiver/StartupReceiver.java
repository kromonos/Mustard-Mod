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

package org.mustard.android.receiver;

import org.mustard.android.service.MultiMention;
import org.mustard.android.service.OAuthKeysService;
import org.mustard.android.service.Version;
import org.mustard.util.MustardUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

	private static String TAG = StartupReceiver.class.getCanonicalName();
    private static boolean hasStartedUp = false;

    @Override
    /** Called when the system is started up */
    public void onReceive(Context context, Intent intent) {
    	MultiMention.schedule(context);
    }

    /** Called when this application is started up */
    public static void onStartupApplication(final Context context) {
        if(hasStartedUp) {
        	Log.i(TAG, "Called onStartupApplication twice or more");
            return;
        }
        int latestSetVersion = MustardUtil.getCurrentVersion(context);
        int version = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            version = pi.versionCode;
        } catch (Exception e) {
            Log.e(TAG, "Error getting version!", e);
        }

        // if we just got upgraded, set the alarms
        boolean justUpgraded = latestSetVersion != version;
        final int finalVersion = version;
        if(justUpgraded) {
        	MultiMention.schedule(context);
            Version.schedule(context,true);
        	MustardUtil.setCurrentVersion(context, finalVersion);
        } else {
        	Version.schedule(context);
        }
        OAuthKeysService.schedule(context);
        hasStartedUp = true;
    }
}
