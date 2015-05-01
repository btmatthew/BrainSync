package com.example.anonymous.brainsync;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
    /*@Override
    public void onReceive(Context context,Intent intent){
        super.onReceive(context, intent);
        AppWidgetManager manager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName widget = new ComponentName(context.getApplicationContext(),ViewNotes.class);
        int[] appWidget =manager.getAppWidgetIds(widget);
        if(appWidget!=null&&appWidget.length>0){
            Log.i("update", "update");
            onUpdate(context,manager,appWidget);
        }
    }*/
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
        Log.i("update", "update2");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.view_notes);

        Intent intent = new Intent(context, WidgetService.class);
        Intent newEntry = new Intent(context, AddEntryActivity.class);
        Intent viewEntry = new Intent(context, DisplaySelectedItem.class);
        Log.i("update", "update3");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,newEntry,0);
        PendingIntent startActivity = PendingIntent.getActivity(context,0,viewEntry,PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i("update", "update4");


        views.setPendingIntentTemplate(R.id.listEntriesViewWidget,startActivity);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.listEntriesViewWidget,intent);
        views.setOnClickPendingIntent(R.id.addNewNote,pendingIntent);
        Log.i("update", "update5");
        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listEntriesViewWidget);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


