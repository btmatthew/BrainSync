package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
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
    Calendar calendar;
    Calendar calendar1;
    int alarmid;
    int notifid;
    int pendingcode;
    int chosenDayOfMonth, chosenMonth, chosenYear, chosenMinute, chosenHour;
    int currentDayOfMonth, currentMonth, currentYear, currentMinute, currentHour;
    Context context = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Intent intent = getIntent();
        entry = intent.getStringExtra("EntryTitle");
        timePicker = (TimePicker) findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        datePicker = (DatePicker) findViewById(R.id.datepicker);
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_accept))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
                alarmid = sharedpreferences.getInt(alarm, 0);
                notifid = sharedpreferences.getInt(notification, 0);
                pendingcode = sharedpreferences.getInt(pendingnotification, 0);




                //get current date and time
                calendar = Calendar.getInstance();
                calendar1 = Calendar.getInstance();
                //sets the time for the alarm to trigger

                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);



                chosenDayOfMonth = datePicker.getDayOfMonth();
                chosenMonth = datePicker.getMonth() + 1;
                chosenYear = datePicker.getYear();
                chosenMinute = timePicker.getCurrentMinute();
                chosenHour=timePicker.getCurrentHour();

                currentYear = calendar1.get(Calendar.YEAR);
                currentMonth = calendar1.get(Calendar.MONTH)+1;
                currentDayOfMonth = calendar1.get(Calendar.DAY_OF_MONTH);
                currentHour = calendar1.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar1.get(Calendar.MINUTE);

                Log.d("currentTime","Y "+currentYear+"  M "+currentMonth+" D "+ currentDayOfMonth+" H "+currentHour+" M "+currentMinute);
                Log.d("chosenTime","Y "+chosenYear+"  M "+chosenMonth+" D "+ chosenDayOfMonth+" H "+chosenHour+" M "+chosenMinute);
                if(chosenYear<=currentYear){
                    if(chosenMonth<=currentMonth){
                        if(chosenDayOfMonth<=currentDayOfMonth){
                            if(chosenHour<=currentHour){
                                if(chosenMinute<=currentMinute){
                                    Toast.makeText(Reminder.this, "Great Scott!, We haven't hit 88MPH yet!, Please set reminder in future.", Toast.LENGTH_SHORT).show();
                                }else{
                                    setReminder();
                                }
                            }else{
                                setReminder();
                            }
                        }else{
                            setReminder();
                        }
                    }else{
                        setReminder();
                    }
                }else{
                    setReminder();
                }

            }

        });
    }
    public void setReminder(){
        //use the AlarmManager to trigger an alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //PendingIntent to launch activity when the alarm triggers
        Intent launch = new Intent(Reminder.this, DisplayNotification.class);
        launch.putExtra("NotifID", notifid);
        launch.putExtra("Title", entry);
        launch.putExtra("Pending", pendingcode);

        // Log.d("TAG", "Setting: notifID-"+String.valueOf(notifid)+" title-"+entry+" pending-"+pendingcode+" alarmid-"+alarmid);

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

        newEntryThread(entry, Reminder.this);
//
        Toast.makeText(Reminder.this, "Reminder Set Successfully :)", Toast.LENGTH_SHORT).show();
        //Go back to the entry
//                    Intent intent = new Intent(Reminder.this, DisplaySelectedItem.class);
//                    intent.putExtra("EXTRA_MESSAGE", entry);
//                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        Reminder.super.onBackPressed();
    }
    public void cancelReminder(Context context, String title, long alarmCode) {

        Log.d("TAG", title + " " + String.valueOf(alarmCode));
        if (Reminder.this == null) {
            Log.d("TAG", "Null");
        }
        int uniqueNumber = (int) alarmCode;
        Intent launch = new Intent(Reminder.this, DisplayNotification.class);
        launch.putExtra("NotifID", uniqueNumber);
        launch.putExtra("Title", title);
        launch.putExtra("Pending", uniqueNumber);

        PendingIntent alarmOff = PendingIntent.getService(context, uniqueNumber, launch, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.cancel(alarmOff);
    }


    private void newEntryThread(final String title, final Context context) {
        new Thread(new Runnable() {
            public void run() {
                //  Log.d("TAG", "THREAD STARTED");
                Time now = new Time();
                now.setToNow();
                Long time = now.toMillis(false);
                Filenames filenames = new Filenames();
                filenames.setFilename(title);
                filenames.setReminderIndicatorValue(1);
                filenames.setReminderCreationTime(time.toString());
                String scheduledTime = chosenHour + ":" + chosenMinute + ", " + chosenDayOfMonth + "/" + chosenMonth + "/" + chosenYear;
                filenames.setReminderScheduledTime(scheduledTime);
                DatabaseAdapter db = new DatabaseAdapter(context);
                db.addReminderEntry(filenames, context);
            }
        }).start();

    }


}
