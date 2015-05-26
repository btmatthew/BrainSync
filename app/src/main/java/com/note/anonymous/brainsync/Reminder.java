package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class Reminder extends Activity {

    TimePicker timePicker;
    DatePicker datePicker;
    private String entry;
    int alarmid =1;
    int notifid =1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Intent intent = getIntent();
        entry = intent.getStringExtra(DisplaySelectedItem.EXTRA_MESSAGE);
        //---Button view---
        Button set = (Button) findViewById(R.id.setreminder);
        set.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                timePicker = (TimePicker) findViewById(R.id.timepicker);
                datePicker = (DatePicker) findViewById(R.id.datepicker);

                //---use the AlarmManager to trigger an alarm---
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                //---get current date and time---
                Calendar calendar = Calendar.getInstance();

                //---sets the time for the alarm to trigger---
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);

                //---PendingIntent to launch activity when the alarm triggers-
//                Intent intent = new Intent("com.note.anonymous.DisplayReminder");
//                intent.putExtra("NotifID", notifid);
                PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), alarmid, new Intent("com.note.anonymous.DisplayReminder")
                        .putExtra("NotifID", notifid).putExtra("Title", entry), 0);



                //---sets the alarm to trigger---
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), displayIntent);


                //Go back to the entry
                Intent intent = new Intent(Reminder.this, DisplaySelectedItem.class);
                intent.putExtra("EXTRA_MESSAGE", entry);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
