package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class EditActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.anonymous.brainsync.MESSAGE1";
    private EditText titlefield;
    private EditText datafield;
    private String originalTitle;
    private String originalBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        originalTitle = intent.getStringExtra(EXTRA_MESSAGE);
        String body = intent.getStringExtra(EXTRA_MESSAGE1);
        originalBody = body;
        titlefield = ((EditText)findViewById(R.id.editTextTitle));
        datafield = ((EditText)findViewById(R.id.editTextBody));
        titlefield.setText(originalTitle);
        datafield.setText(body);
    }

    public void updateEntryMethod(View view) {

        //Get user inputs from the EditText fields
        final String editedTitle = titlefield.getText().toString().trim();
        final String information = datafield.getText().toString().trim();

        if(editedTitle.equals("")) {
            Toast.makeText(this, "Title cannot be empty :)", Toast.LENGTH_LONG).show();

        } else {
            if(!(originalTitle).equals(editedTitle)){
                new AlertDialog.Builder(this)
                        .setTitle("Title updated")
                        .setMessage("What would you like to do?")
                        .setNegativeButton("Create new note\n and keep existing.", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){
                                writeFile(editedTitle, information);
                            }
                        })
                        .setPositiveButton("Overwrite the existing note.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            File from = new File(getString(R.string.directoryLocation)+originalTitle);
                            File to = new File(getString(R.string.directoryLocation)+editedTitle);
                                from.renameTo(to);
                                writeFile(editedTitle, information);

                            }
                        }).create().show();
            }else{
                writeFile(editedTitle,information);
            }
        }
    }
    private void writeFile(String title,String information){

        try {
            //Create a file and write to it. Input in the Title EditText field is used as file name
            FileOutputStream updateEntry = openFileOutput(title, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(updateEntry));
            writer.println(information);
            writer.close();

            //Start the success activity after file creation and writing has been done
            callListEntriesActivity();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    private void callListEntriesActivity(){
        Intent intent = new Intent(this, ListEntriesActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void cancelUpdateMethod (View view) {

        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString().trim();

        if(title.equals("") && information.equals("")) {

            EditActivity.super.onBackPressed();

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Cancel Edit?")
                    .setMessage("New information will not be saved. Are you sure you want to cancel?")
                    .setNegativeButton("No, Go Back!", null)
                    .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            EditActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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

    @Override
    public void onBackPressed() {

        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString();

        Log.d("G", String.valueOf(originalBody.length()));
        Log.d("G", String.valueOf(information.length()));

        if(originalTitle.equals(title) || originalBody.equals(information)){

            EditActivity.super.onBackPressed();

        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Cancel Edit?")
                    .setMessage("New information will not be saved. Are you sure you want to cancel?")
                    .setNegativeButton("No, Go Back!", null)
                    .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            EditActivity.super.onBackPressed();
                        }
                    }).create().show();

            }


    }
}
