package com.example.anonymous.brainsync;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Matthew Bulat on 27/04/2015.
 */
public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context=null;
    private int appWidgetId;
    private List<String> widgetList;



    public WidgetRemoteViewsFactory(Context context, Intent intent){
        this.context=context;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        updateWidgetListView();
    }



    private void updateWidgetListView(){
        String fileDirectory = "data/data/com.example.anonymous.brainsync/files/";
        File dir = new File(fileDirectory);
        String[] fileList = dir.list();
        Arrays.sort(fileList, Collator.getInstance());
        widgetList = new ArrayList<String>(Arrays.asList(fileList));
        for (Iterator<String> it = widgetList.iterator(); it.hasNext();) {
            String item = it.next();
            if(item.contains("rList")){
                it.remove();
            }else if(item.contains("share_history")){
                it.remove();
            }
        }

    }
    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        widgetList.clear();
    }

    @Override
    public int getCount() {
        return widgetList.size();
    }
    @Override
    public RemoteViews getViewAt(int position) {
        String title=widgetList.get(position);
        Intent viewEntry = new Intent(context, DisplaySelectedItem.class);
        viewEntry.putExtra(ViewNotes.EXTRA_MESSAGE,title);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.row_for_widget);
        remoteViews.setTextViewText(R.id.heading, title);
        remoteViews.setOnClickFillInIntent(R.id.heading,viewEntry);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
