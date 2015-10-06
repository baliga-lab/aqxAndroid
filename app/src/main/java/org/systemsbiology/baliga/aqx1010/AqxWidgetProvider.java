package org.systemsbiology.baliga.aqx1010;

import java.util.HashMap;
import java.util.Map;
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

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * This is the widget provider class.
 */
public class AqxWidgetProvider extends AppWidgetProvider {

    private static int counter = 0;
    private float o2vals[] = {10.2f, 8.8f, 3.3f};

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, AqxWidgetProvider.class);
        for (int widgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.aqx_widget);
            // Set the text
            remoteViews.setTextViewText(R.id.temperature, "23.2\u00B0");
            float o2val = o2vals[counter % 3];
            remoteViews.setTextViewText(R.id.o2, String.format("%.2f", o2val));
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
            counter++;
            Log.d("aqx", "widget");
            if (o2val <= 3.5) {
                final Intent pIntent = new Intent("com.getpebble.action.SEND_NOTIFICATION");
                final Map<String, String> data = new HashMap<String, String>();
                data.put("title", "AQX Alert");
                data.put("body", "Something is wrong with your Aquaponics system. Please check.");
                final JSONObject jsonData = new JSONObject(data);
                final String notificationData = new JSONArray().put(jsonData).toString();
                pIntent.putExtra("messageType", "PEBBLE_ALERT");
                pIntent.putExtra("sender", "AqxWidget");
                pIntent.putExtra("notificationData", notificationData);
                context.sendBroadcast(pIntent);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.leaf_icon)
                        .setContentTitle("AQX Alert")
                        .setContentText("Something is wrong with your Aquaponics system. Please check.");

                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(1, mBuilder.build());
            }
        }
    }
}