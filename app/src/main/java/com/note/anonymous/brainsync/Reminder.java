package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Reminder extends Activity {

    TimePicker timePicker;
    DatePicker datePicker;
    private String entry;
    final String AppPrefs = "AppPrefs";
    SharedPreferences sharedpreferences;
    String alarm = "alarmKey";
    String notification = "notificationKey";
    String pendingnotification = "pendingKey";

    int alarmid;
    int notifid;
    int pendingcode;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Intent intent = getIntent();
        entry = intent.getStringExtra("EntryTitle");
        timePicker = (TimePicker) findViewById(R.id.timepicker);
        datePicker = (DatePicker) findViewById(R.id.datepicker);

        Button set = (Button) findViewById(R.id.setreminder);
        set.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                    sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
                    alarmid = sharedpreferences.getInt(alarm, 0);
                    notifid = sharedpreferences.getInt(notification, 0);
                    pendingcode = sharedpreferences.getInt(pendingnotification, 0);


                    //use the AlarmManager to trigger an alarm
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    //get current date and time
                    Calendar calendar = Calendar.getInstance();

                    //sets the time for the alarm to trigger
                    calendar.set(Calendar.YEAR, datePicker.getYear());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);

                    //PendingIntent to launch activity when the alarm triggers
                    Intent launch = new Intent(Reminder.this, DisplayNotification.class);
                    launch.putExtra("NotifID", notifid);
                    launch.putExtra("Title", entry);
                    launch.putExtra("Pending", pendingcode);

                    PendingIntent alarmOff = PendingIntent.getService(getBaseContext(), alarmid, launch, 0);

                    //sets the alarm to trigger
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmOff);

                    alarmid = alarmid + 1;
                    notifid = notifid + 1;
                    pendingcode = pendingcode + 1;
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(alarm, alarmid);
                    editor.putInt(notification, notifid);
                    editor.putInt(pendingnotification, pendingcode);
                editor.apply();

                    Toast.makeText(Reminder.this, "Reminder Set Successfully :)", Toast.LENGTH_SHORT).show();
                    //Go back to the entry
//                    Intent intent = new Intent(Reminder.this, DisplaySelectedItem.class);
//                    intent.putExtra("EXTRA_MESSAGE", entry);
//                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                Reminder.super.onBackPressed();
                }

        });


    }




}
