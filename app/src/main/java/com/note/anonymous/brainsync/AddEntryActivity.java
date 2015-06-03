package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;


public class AddEntryActivity extends Activity {

    final String AppPrefs = "AppPrefs";
    SharedPreferences sharedpreferences;
    String alarm = "alarmKey";
    String notification = "notificationKey";
    String pendingnotification = "pendingKey";

    int alarmid;
    int notifid;
    int pendingcode;

    int dayOfMonth, month, year, currentMinute, currentHour;
    int alarmset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if(Intent.ACTION_SEND.equals(action)&&type!=null){
            if("text/plain".equals(type)){
                setTextFromShare(intent);
            }
        }
        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add New Entry");

        Button reminder = (Button) findViewById(R.id.setaddreminder);

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(AddEntryActivity.this);
                final DatePicker datePicker = new DatePicker(AddEntryActivity.this);
                final TimePicker timePicker = new TimePicker(AddEntryActivity.this);

                LinearLayout linearlayout = new LinearLayout(AddEntryActivity.this);
                linearlayout.setOrientation(LinearLayout.VERTICAL);
                linearlayout.addView(datePicker);
                linearlayout.addView(timePicker);
                dialog.setView(linearlayout);
                dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dayOfMonth = datePicker.getDayOfMonth();
                        month = datePicker.getMonth();
                        year = datePicker.getYear();
                        currentMinute = timePicker.getCurrentMinute();
                        currentHour = timePicker.getCurrentHour();

                        if (alarmset == 0) {
                            Toast.makeText(AddEntryActivity.this, "Reminder has been set. :)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddEntryActivity.this, "Reminder date/time has been updated. :)", Toast.LENGTH_SHORT).show();
                        }

                        alarmset = 1;
//                        Log.d("TAG", String.valueOf(dayOfMonth));
//                        Log.d("TAG", String.valueOf(month));
//                        Log.d("TAG", String.valueOf(year));
//                        Log.d("TAG", String.valueOf(currentMinute));
//                        Log.d("TAG", String.valueOf(currentHour));

                    }
                });

                dialog.setNegativeButton("Cancel", null);

                dialog.show();

            }
        });
    }

    public void chooseSaveMethod(View view){
        switch (alarmset){
            case 0:
                saveEntryMethod(view);
                Log.d("TAG", "Old Method");
                break;
            case 1:
                saveEntryMethod1(view);
                Log.d("TAG", "New Method");
        }


    }


    private void setTextFromShare(Intent intent){
        EditText datafield = (EditText) findViewById(R.id.information);
        String sharedtext = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedtext!=null){
            datafield.setText(sharedtext);
        }

    }
    public void saveEntryMethod(View view) {

        //Link local EditText variables to EditText views created in XML
        EditText titlefield = (EditText) findViewById(R.id.titleBar);
        EditText datafield = (EditText) findViewById(R.id.information);


        //Get user inputs from the EditText fields
        final String title = titlefield.getText().toString().trim();
        final String information = datafield.getText().toString().trim();

        if (title.equals("")) {
            Toast.makeText(this, "Title cannot be empty :)", Toast.LENGTH_LONG).show();

        } else {

                File dir = new File("data/data/com.example.anonymous.brainsync/files");
               // File[] filelist = dir.listFiles();

                if (new DatabaseAdapter(this).getNumberOfRows() == 0) {

                    try {
                        //Add entry to the database on a seperate thread
                        newEntryThread(title, this);

                        //Create a file and write to it. Input in the Title EditText field is used as file name
                        FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                        writer.println(information);
                        writer.close();


                        //Start the success activity after file creation and writing has been done
                        finish();
                        Intent intent = new Intent(this, SuccessActivity.class);
                        startActivity(intent);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                } else {

                    if (new DatabaseAdapter(this).searchByTitle(title)) {

                        new AlertDialog.Builder(this)
                                .setTitle("Hold Up...")
                                .setMessage("An entry for '" + title + "' already exists. Saving this with the same name will overwrite the previous one. Do you wish to continue?")
                                .setNegativeButton("No, Go Back!", null)
                                .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        overwriteMethod(title,information);
                                    }
                                }).create().show();

                    } else {

                        try {
                            newEntryThread(title, this);
                            //Create a file and write to it. Input in the Title EditText field is used as file name
                            FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                            PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                            writer.println(information);
                            writer.close();


                            //Start the success activity after file creation and writing has been done
                            finish();
                            Intent intent = new Intent(this, SuccessActivity.class);
                            startActivity(intent);

                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                    }



            }
        }
    }

    public void saveEntryMethod1(View view) {

        //Link local EditText variables to EditText views created in XML
        EditText titlefield = (EditText) findViewById(R.id.titleBar);
        EditText datafield = (EditText) findViewById(R.id.information);


        //Get user inputs from the EditText fields
        final String title = titlefield.getText().toString().trim();
        final String information = datafield.getText().toString().trim();

        sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
        alarmid = sharedpreferences.getInt(alarm, 0);
        notifid = sharedpreferences.getInt(notification, 0);
        pendingcode = sharedpreferences.getInt(pendingnotification, 0);


        //use the AlarmManager to trigger an alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //get current date and time
        Calendar calendar = Calendar.getInstance();

        //sets the time for the alarm to trigger
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, currentHour);
        calendar.set(Calendar.MINUTE, currentMinute);
        calendar.set(Calendar.SECOND, 0);

        //PendingIntent to launch activity when the alarm triggers
        Intent launch = new Intent(AddEntryActivity.this, DisplayNotification.class);
        launch.putExtra("NotifID", notifid);
        launch.putExtra("Title", title);
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
        editor.commit();



        if (title.equals("")) {
            Toast.makeText(this, "Title cannot be empty :)", Toast.LENGTH_LONG).show();

        } else {

            File dir = new File("data/data/com.example.anonymous.brainsync/files");
            // File[] filelist = dir.listFiles();

            if (new DatabaseAdapter(this).getNumberOfRows() == 0) {

                try {
                    //Add entry to the database on a seperate thread
                    newEntryThread(title, this);

                    //Create a file and write to it. Input in the Title EditText field is used as file name
                    FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                    writer.println(information);
                    writer.close();


                    //Start the success activity after file creation and writing has been done
                    finish();
                    Intent intent = new Intent(this, SuccessActivity.class);
                    startActivity(intent);

                } catch (IOException e) {

                    e.printStackTrace();
                }
            } else {

                if (new DatabaseAdapter(this).searchByTitle(title)) {

                    new AlertDialog.Builder(this)
                            .setTitle("Hold Up...")
                            .setMessage("An entry for '" + title + "' already exists. Saving this with the same name will overwrite the previous one. Do you wish to continue?")
                            .setNegativeButton("No, Go Back!", null)
                            .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    overwriteMethod(title,information);
                                }
                            }).create().show();

                } else {

                    try {
                        newEntryThread(title, this);
                        //Create a file and write to it. Input in the Title EditText field is used as file name
                        FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                        writer.println(information);
                        writer.close();


                        //Start the success activity after file creation and writing has been done
                        finish();
                        Intent intent = new Intent(this, SuccessActivity.class);
                        startActivity(intent);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }



            }
        }
    }


    private void overwriteMethod(String title, String information) {
        try {
            updateEditDate(title,this);
            FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
            writer.println(information);
            writer.close();


            //Start the success activity after file creation and writing has been done
            finish();
            Intent intent = new Intent(AddEntryActivity.this, SuccessActivity.class);
            startActivity(intent);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    //Cancels entry and returns user to the MainActivity
    public void cancelEntryMethod(View view) {

        EditText titlefield = (EditText) findViewById(R.id.titleBar);
        EditText datafield = (EditText) findViewById(R.id.information);

        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString().trim();

        if(title.equals("") && information.equals("")) {

            AddEntryActivity.super.onBackPressed();

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Cancel Edit?")
                    .setMessage("All information on this page will not be saved. Are you sure you want to cancel this entry?")
                    .setNegativeButton("No, Go Back!", null)
                    .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            AddEntryActivity.super.onBackPressed();
                        }
                    }).create().show();
        }

    }

    @Override
    public void onBackPressed() {

        EditText titlefield = (EditText) findViewById(R.id.titleBar);
        EditText datafield = (EditText) findViewById(R.id.information);

        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString().trim();

        if(title.equals("") && information.equals("")) {

            AddEntryActivity.super.onBackPressed();

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Cancel Edit?")
                    .setMessage("All information on this page will not be saved. Are you sure you want to discard this entry?")
                    .setNegativeButton("No, Go Back!", null)
                    .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            AddEntryActivity.super.onBackPressed();
                        }
                    }).create().show();
        }

        }

    //Thread used for purpose of adding entry to the database
    private void newEntryThread(final String title, final Context context){
        new Thread(new Runnable() {
            public void run() {
                Time now = new Time();
                now.setToNow();
                Long time = now.toMillis(false);
                Filenames filenames = new Filenames();

                filenames.setFilename(title);
                filenames.setCreationDate(time);
                filenames.setFileTypeText();
                DatabaseAdapter db = new DatabaseAdapter(context);
                db.addEntry(filenames);
            }
        }).start();
    }
    private void updateEditDate(final String title, final Context context){
        new Thread(new Runnable() {
            public void run() {
                Time now = new Time();
                now.setToNow();
                Long time = now.toMillis(false);
                Filenames filenames = new Filenames();

                filenames.setFilename(title);
                filenames.setEditedDate(time);
                DatabaseAdapter db = new DatabaseAdapter(context);
                db.updateEditDate(filenames);
            }
        }).start();
    }
}
