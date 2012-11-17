package org.mumod.android.widget;

import org.mumod.android.R;
import org.mumod.android.activity.MustardUpdate;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class Composer  extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager awm, int[] i){
		super.onUpdate(context,awm,i);
		Log.d("Mustard", "Widget onUpdate()");
		RemoteViews updateViews = null;
		
		ComponentName thisWidget = new ComponentName(context, Composer.class);
		
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_med);
		updateViews.setTextViewText(R.id.message,"Compose..");
		Intent defineIntent = MustardUpdate.getActionCompose(context, null);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0 /* no requestCode */, defineIntent, 0 /* no flags */);
        updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
        awm.updateAppWidget(thisWidget, updateViews);
	}

	@Override
	public void onEnabled(Context context){
		super.onEnabled(context);
		Log.d("Mustard", "Widget onEnabled()");
		RemoteViews updateViews = null;
		
		ComponentName thisWidget = new ComponentName(context, Composer.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_med);
		updateViews.setTextViewText(R.id.message,"Compose..");
		Intent defineIntent = MustardUpdate.getActionCompose(context, null);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0 /* no requestCode */, defineIntent, 0 /* no flags */);
        updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
        manager.updateAppWidget(thisWidget, updateViews);

	}
	

}
