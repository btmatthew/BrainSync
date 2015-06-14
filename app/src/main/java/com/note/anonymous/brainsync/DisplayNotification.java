package com.note.anonymous.brainsync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Anonymous on 01/06/2015.
 */
public class DisplayNotification extends IntentService {


    public DisplayNotification(){
        super("DisplayNotification");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        //Log.d("TAG", "DisplayNotification Service Started");
        int notifID = intent.getExtras().getInt("NotifID");
        String title = intent.getExtras().getString("Title");
        int requestCode = intent.getExtras().getInt("Pending");

        //PendingIntent to launch activity if the user selects the notification
        Intent intent1 = new Intent(this, DisplaySelectedItem.class);
        intent1.putExtra("EXTRA_MESSAGE", title);

        PendingIntent notificationIntent = PendingIntent.getActivity(this, requestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri nSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Tap to view entry")
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 250, 100, 500});

        String notify = "notifyKey";
        final String AppPrefs = "AppPrefs";
        SharedPreferences sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);;
        int notificationsound = sharedpreferences.getInt(notify, 1);

        if(notificationsound == 1){
            mBuilder.setSound(nSound);
        }


        mBuilder.setContentIntent(notificationIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify(notifID, mBuilder.build());

    }

}
