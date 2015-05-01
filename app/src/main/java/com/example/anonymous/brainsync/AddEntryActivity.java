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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class AddEntryActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add New Entry");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_entry, menu);
        return true;
    }

    public void saveEntryMethod(View view) {

        //Link local EditText variables to EditText views created in XML
        EditText titlefield = (EditText) findViewById(R.id.titleBar);
        EditText datafield = (EditText) findViewById(R.id.information);

        //Get user inputs from the EditText fields
        final String title = titlefield.getText().toString().trim();
        final String information = datafield.getText().toString().trim();

        if(title.equals("") || information.equals("")) {
            Toast.makeText(this, "Fields Cannot Be Empty :)", Toast.LENGTH_LONG).show();

        } else {
            try {
            //      String fileDirectory = getString(R.string.directoryLocation);
            File dir = new File("data/data/com.example.anonymous.brainsync/files");
            File[] filelist = dir.listFiles();

            String[] a = new String[filelist.length];
            for (int i = 0; i < a.length; i++) {
                a[i] = filelist[i].getName();
            }

            if (Arrays.asList(a).contains(title)) {

                new AlertDialog.Builder(this)
                        .setTitle("Hold Up...")
                        .setMessage("An entry for '" + title + "' already exists. Saving this with the same name will overwrite the previous one. Do you wish to continue?")
                        .setNegativeButton("No, Go Back!", null)
                        .setPositiveButton("Yes, Please!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                overwriteMethod();
                            }

                            private void overwriteMethod() {
                                try {

                                    FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                                    writer.println();
                                    writer.println(information);
                                    writer.close();


                                    //Start the success activity after file creation and writing has been done
                                    Intent intent = new Intent(AddEntryActivity.this, SuccessActivity.class);
                                    startActivity(intent);

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                            }
                        }).create().show();

            } else {

                try {

                    //Create a file and write to it. Input in the Title EditText field is used as file name
                    FileOutputStream createEntry = openFileOutput(title, Context.MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                    writer.println();
                    writer.println(information);
                    writer.close();


                    //Start the success activity after file creation and writing has been done
                    Intent intent = new Intent(this, SuccessActivity.class);
                    startActivity(intent);

                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        } catch (NullPointerException a) {

                Toast.makeText(this, "Exception Caught", Toast.LENGTH_LONG).show();
            }

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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        //If the selected menu item is search launch the search bar at the top of the screen. See this section in MainActivity for more explanation
        switch(id){
            case R.id.search:
                List<String> list = new ArrayList<String>(Arrays.asList(fileList()));

                for (Iterator<String> it = list.iterator(); it.hasNext();) {
                    if (it.next().contains("rList"))
                        it.remove();
                }

                final String[] defaultFiles = new String[list.size()];
                int a = defaultFiles.length;

                if (a == 0) {
                    Toast.makeText(this, "No Entries Yet", Toast.LENGTH_SHORT).show();
                } else {
                    onSearchRequested();
                }
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(this, About.class);
                startActivity(intent1);

        }
        return super.onOptionsItemSelected(item);
    }


}
