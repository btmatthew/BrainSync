package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;


public class AddEntryActivity extends Activity {

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
                File[] filelist = dir.listFiles();



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
