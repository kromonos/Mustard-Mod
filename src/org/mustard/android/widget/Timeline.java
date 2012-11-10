package org.mustard.android.widget;

import org.mustard.android.R;
import org.mustard.android.activity.MustardUpdate;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class Timeline  extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager awm, int[] i){
		super.onUpdate(context,awm,i);
		Log.d("Mustard", "Widget Timeline onUpdate()");
		RemoteViews updateViews = null;
		
		ComponentName thisWidget = new ComponentName(context, Timeline.class);
		
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_notice);
		
		Intent defineIntent = MustardUpdate.getActionCompose(context, null);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0 /* no requestCode */, defineIntent, 0 /* no flags */);
        updateViews.setOnClickPendingIntent(R.id.buttoncompose, pendingIntent);
        
        awm.updateAppWidget(thisWidget, updateViews);
	}

}
