package com.example.Cortex.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.Cortex.MainActivity;
import com.example.Cortex.R;

/**
 * Home-screen widget for Cortex.
 * A single tappable card that opens the app.
 */
public class CortexWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context,
                              AppWidgetManager appWidgetManager,
                              int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
            R.layout.widget_cortex);

        // Open the app when tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        views.setOnClickPendingIntent(R.id.widgetRoot, pending);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}