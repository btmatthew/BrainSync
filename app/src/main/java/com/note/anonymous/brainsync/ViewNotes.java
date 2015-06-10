package com.note.anonymous.brainsync;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ViewNotes extends AppWidgetProvider {
    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.view_notes);

        Intent intent = new Intent(context, WidgetService.class);
        Intent newEntry = new Intent(context, AddEntryActivity.class);
        Intent openApp = new Intent(context,MainActivity.class);
        Intent viewEntry = new Intent(context, DisplaySelectedItem.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,newEntry,0);
        PendingIntent startActivity = PendingIntent.getActivity(context,0,viewEntry,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent startApp = PendingIntent.getActivity(context,0,openApp,0);


        views.setPendingIntentTemplate(R.id.listEntriesViewWidget,startActivity);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.listEntriesViewWidget,intent);
        views.setOnClickPendingIntent(R.id.addNewNote,pendingIntent);
        views.setOnClickPendingIntent(R.id.title,startApp);
        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listEntriesViewWidget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


