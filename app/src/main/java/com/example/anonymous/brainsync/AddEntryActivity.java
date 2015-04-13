package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Context;
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


public class AddEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        String title = titlefield.getText().toString().trim();
        String information = datafield.getText().toString().trim();

        if(title.equals("") || information.equals("")) {
            Toast.makeText(this, "Fields Cannot Be Empty :)", Toast.LENGTH_LONG).show();

        } else {

            try {

                //Create a file and write to it. Input in the Title EditText field is used as file name
               FileOutputStream createEntry = openFileOutput(title+".txt", Context.MODE_PRIVATE);
               PrintWriter writer = new PrintWriter(new OutputStreamWriter(createEntry));
                writer.println(title);
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






    }

    //Cancels entry and returns user to the MainActivity
    public void cancelEntryMethod(View view) {

       Intent intent = new Intent(this, MainActivity.class);
        //setFlags method is used so as not to create a new MainActivity and add to the activity stack but rather clear all previous activities
        //and launch the MainActivity as the only activity on the stack
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //If the selected menu item is search launch the search bar at the top of the screen. See this section in MainActivity for more explanation
        if (id == R.id.search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
