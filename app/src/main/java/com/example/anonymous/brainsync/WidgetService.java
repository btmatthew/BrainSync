package com.example.anonymous.brainsync;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Matthew Bulat on 27/04/2015.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(),intent));
    }
}
