package com.example.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    //Called when the 'Add An Entry' button is clicked as configured in the XML
    public void addEntry (View entry) {

        Intent intent = new Intent(this, AddEntryActivity.class);
        startActivity(intent);

    }

    //Called when the 'List All Entries' button is clicked as configured in the XML
    public void listEntriesMethod(View view){

        Intent intent = new Intent(this, ListEntriesActivity.class);
        startActivity(intent);


    }

    //Called when the 'Search My Brain' button is clicked as configured in the XML
    public void goSearch(View view) {

        onSearchRequested();
        //Intent intent = new Intent(this, SearchActivity.class);
       // startActivity(intent);

    }
    //Calls settings activity
    public void goSettings(){

        //onSearchRequested();
        Intent intent = new Intent(this, Settings.class);
         startActivity(intent);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //If the selected menu item is search launch the search bar at the top of the screen
        //Each time the user searches for something anywhere in the app, the android system automatically creates an intent and passes it to the
        //SearchActivity. Coding for this is in the IntentFilter and MetaData tag contained in the Manifest File under the SearchActivity tag
        switch(id){
            case R.id.search:
                onSearchRequested();
                //return true;
                break;
            case R.id.action_settings:
                goSettings();
                break;

        }
        /*if (id == R.id.search) {
            onSearchRequested();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Stop BrainSyncing?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
