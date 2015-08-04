package org.systemsbiology.baliga.aqx1010;

import java.util.Random;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.app.PendingIntent;
import android.widget.RemoteViews;


/**
 * This is the widget provider class.
 */
public class AqxWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, AqxWidgetProvider.class);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.leaf_icon)
                .setContentTitle("My notification")
                .setContentText("some text");
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.aqx_widget);
            // Set the text
            remoteViews.setTextViewText(R.id.temperature, "23.2\u00B0");
            remoteViews.setTextViewText(R.id.o2, "2.3");
            remoteViews.setTextViewText(R.id.ammonium, "3.2");
            remoteViews.setTextViewText(R.id.nitrate, "42.3");
            remoteViews.setTextViewText(R.id.ph, "6.3");

            // Register an onClickListener
            Intent intent = new Intent(context, AqxWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.temperature, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.o2, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            // set notification
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(1, mBuilder.build());
        }
    }
}