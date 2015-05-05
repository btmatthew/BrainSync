package com.example.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class EditActivity extends Activity {
    private EditText titlefield;
    private EditText datafield;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        String title = intent.getStringExtra(DisplaySelectedItem.EXTRA_MESSAGE);
        String body = intent.getStringExtra(DisplaySelectedItem.EXTRA_MESSAGE1);
        titlefield = ((EditText)findViewById(R.id.editTextTitle));
        datafield = ((EditText)findViewById(R.id.editTextBody));
        titlefield.setText(title);
        datafield.setText(body);


    }

    public void updateEntryMethod(View view) {

        //Get user inputs from the EditText fields
        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString().trim();

        if(title.equals("") || information.equals("")) {
            Toast.makeText(this, "Fields Cannot Be Empty :)", Toast.LENGTH_LONG).show();

        } else {

            try {

                //Create a file and write to it. Input in the Title EditText field is used as file name
                FileOutputStream updateEntry = openFileOutput(title, Context.MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(updateEntry));
                writer.println();
                writer.println(information);
                writer.close();

                //Start the success activity after file creation and writing has been done
                Intent intent = new Intent(this, ListEntriesActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

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

//        Intent intent = new Intent(this, ListEntriesActivity.class);
//        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
}
