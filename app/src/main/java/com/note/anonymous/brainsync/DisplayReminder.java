package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Anonymous on 21/05/2015.
 */
public class DisplayReminder extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //---get the notification ID for the notification;
        // passed in by the MainActivity---
        int notifID = getIntent().getExtras().getInt("NotifID");
        String title = getIntent().getExtras().getString("Title");

        //---PendingIntent to launch activity if the user selects
        // the notification---
        Intent intent = new Intent(this, DisplaySelectedItem.class);
        intent.putExtra("EXTRA_MESSAGE", title);


//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(DisplaySelectedItem.class);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notifID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //intent.putExtra("NotifID", notifID);


        PendingIntent detailsIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Uri nSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText("Tap to view entry")
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 100, 250, 100, 500})
                        .setSound(nSound);


        mBuilder.setContentIntent(detailsIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify(notifID, mBuilder.build());

        finish();
    }
}


