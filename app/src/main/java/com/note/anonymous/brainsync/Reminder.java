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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
    long timeremaining;
    Context context = this;
    int source;
    long code;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Intent intent = getIntent();
        entry = intent.getStringExtra("EntryTitle");
        source = intent.getIntExtra("Source", 0);
        if (source != 0) {
            code = intent.getLongExtra("Code", 0);
        }
        timePicker = (TimePicker) findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        datePicker = (DatePicker) findViewById(R.id.datepicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        FloatingActionButton fabButton1 = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_cancel))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.LEFT)
                .withMargins(16, 0, 0, 16)
                .create();

        fabButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reminder.super.onBackPressed();
            }
        });

        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_accept))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (source == 0) {
                    sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
                    alarmid = sharedpreferences.getInt(alarm, 0);
                    notifid = sharedpreferences.getInt(notification, 0);
                    pendingcode = sharedpreferences.getInt(pendingnotification, 0);
                }
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

                timeremaining = calendar.getTimeInMillis() - calendar1.getTimeInMillis();

                if (calendar.before(calendar1)) {
                    Toast.makeText(Reminder.this, "Great Scott!, We haven't hit 88MPH yet!, Please set reminder in future.", Toast.LENGTH_SHORT).show();
                } else {
                    setReminder();
                }
            }

        });
    }

    public void setReminder() {
        //use the AlarmManager to trigger an alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (source == 0) {
            //PendingIntent to launch activity when the alarm triggers
            Intent launch = new Intent(Reminder.this, DisplayNotification.class);
            launch.putExtra("NotifID", notifid);
            launch.putExtra("Title", entry);
            launch.putExtra("Pending", pendingcode);

//        PendingIntent alarmOff = PendingIntent.getService(getBaseContext(), alarmid, launch, 0);
            Log.d("TAG", "Setting: notifID-" + String.valueOf(notifid) + " title-" + entry + " pending-" + pendingcode + " alarmid-" + alarmid);

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
            long seconds = timeremaining / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (hours == 0) {
                Toast.makeText(Reminder.this, "Reminder set for " + String.format("%d minute(s), %d second(s) from now",
                        TimeUnit.MILLISECONDS.toMinutes(timeremaining),
                        TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                ), Toast.LENGTH_LONG).show();
            } else if (days == 0) {
                Toast.makeText(Reminder.this, "Reminder set for " + String.format("%d hour(s), %d minute(s), %d second(s) from now",
                        TimeUnit.MILLISECONDS.toHours(timeremaining),
                        TimeUnit.MILLISECONDS.toMinutes(timeremaining) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeremaining)),
                        TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                ), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Reminder.this, "Reminder set for " + String.format("%d day(s), %d hour(s), %d minute(s), %d second(s) from now",
                        TimeUnit.MILLISECONDS.toDays(timeremaining),
                        TimeUnit.MILLISECONDS.toHours(timeremaining) -
                                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeremaining)),
                        TimeUnit.MILLISECONDS.toMinutes(timeremaining) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeremaining)),
                        TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                ), Toast.LENGTH_LONG).show();
            }


        } else {


            int codeInt = (int) code;

            cancelSystemReminder(entry, codeInt);

            int min = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            String minConv;
            if (min >= 0 && min <= 9) {
                minConv = "0" + min;
            } else {
                minConv = "" + min;
            }
            String hourConv;
            if (hour >= 0 && hour <= 9) {
                hourConv = "0" + hour;
            } else {
                hourConv = String.valueOf(hour);
            }
            String scheduledTime = hourConv + ":" + minConv + ", " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);

            DatabaseAdapter edit = new DatabaseAdapter(this);
            int count = edit.editReminderScheduleTime(scheduledTime, code);
            if (count > 0) {
                Intent launch = new Intent(Reminder.this, DisplayNotification.class);
                launch.putExtra("NotifID", codeInt);
                launch.putExtra("Title", entry);
                launch.putExtra("Pending", codeInt);

                PendingIntent alarmOff = PendingIntent.getService(getBaseContext(), codeInt, launch, 0);
                //sets the alarm to trigger
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmOff);

                long seconds = timeremaining / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                if (hours == 0) {
                    Toast.makeText(Reminder.this, "Reminder rescheduled for " + String.format("%d minute(s), %d second(s) from now",
                            TimeUnit.MILLISECONDS.toMinutes(timeremaining),
                            TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                    ), Toast.LENGTH_LONG).show();
                } else if (days == 0) {
                    Toast.makeText(Reminder.this, "Reminder rescheduled for " + String.format("%d hour(s), %d minute(s), %d second(s) from now",
                            TimeUnit.MILLISECONDS.toHours(timeremaining),
                            TimeUnit.MILLISECONDS.toMinutes(timeremaining) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeremaining)),
                            TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                    ), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Reminder.this, "Reminder rescheduled for " + String.format("%d day(s), %d hour(s), %d minute(s), %d second(s) from now",
                            TimeUnit.MILLISECONDS.toDays(timeremaining),
                            TimeUnit.MILLISECONDS.toHours(timeremaining) -
                                    TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeremaining)),
                            TimeUnit.MILLISECONDS.toMinutes(timeremaining) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeremaining)),
                            TimeUnit.MILLISECONDS.toSeconds(timeremaining) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeremaining))
                    ), Toast.LENGTH_LONG).show();
                }

                // Toast.makeText(Reminder.this, "Reminder Successfully Rescheduled :)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Reminder.this, "Sorry, something went wrong :(", Toast.LENGTH_SHORT).show();
            }


        }

        Reminder.super.onBackPressed();
    }

    public void cancelSystemReminder(String title, int code) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent launch = new Intent(this, DisplayNotification.class);
        launch.putExtra("NotifID", code);
        launch.putExtra("Title", title);
        launch.putExtra("Pending", code);
        PendingIntent alarmOff = PendingIntent.getService(this, code, launch, 0);
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
                //user string format
                //http://stackoverflow.com/questions/11599947/calendar-minute-giving-minutes-without-leading-zero
                int min = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                String minConv;
                if (min >= 0 && min <= 9) {
                    minConv = "0" + min;
                } else {
                    minConv = "" + min;
                }
                String hourConv;
                if (hour >= 0 && hour <= 9) {
                    hourConv = "0" + hour;
                } else {
                    hourConv = String.valueOf(hour);
                }
                String scheduledTime = hourConv + ":" + minConv + ", " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);

                filenames.setReminderScheduledTime(scheduledTime);
                DatabaseAdapter db = new DatabaseAdapter(context);
                db.addReminderEntry(filenames, context);
            }
        }).start();

    }

    public void toast() {

    }


}
