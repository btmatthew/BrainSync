package com.note.anonymous.brainsync;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.Random;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    //Called when the 'Add An Entry' button is clicked as configured in the XML
    public void addEntry (View entry) {
        Intent intent = new Intent(this, AddEntryActivity.class);
        startActivity(intent);
    }

    //Called when the 'List All Entries' button is clicked as configured in the XML
    public void listEntriesMethod(View view){
        if (checkFiles()) {
             Intent intent = new Intent(this, ListEntriesActivity.class);
             startActivity(intent);
         }
    }

    //Called when the 'Search My Brain' button is clicked as configured in the XML
    public void goSearch(View view) {
        if (checkFiles()) {
            onSearchRequested();
        }
    }
    public boolean checkFiles(){
        File[]fileList = new File(getString(R.string.directoryLocation)).listFiles();
        int size=0;
        for(int i =0;i<fileList.length;i++){
            if(!(fileList[i].getName().contains("rList")||fileList[i].getName().contains("share_history"))){
                size++;
            }
        }

        if(size>0){
            return true;
        }else{
            Toast.makeText(this, "No Entries Yet", Toast.LENGTH_SHORT).show();
            return false;
        }


    }
    public void openRandomNote(View view){
        if (checkFiles()) {
            File[] fileList = new File(getString(R.string.directoryLocation)).listFiles();
            Random r = new Random();
            String selectedFile = fileList[r.nextInt(fileList.length)].getName();
            while(selectedFile.contains("rList")||selectedFile.contains("share_history")){
                selectedFile = fileList[r.nextInt(fileList.length)].getName();
            }
            Intent intent = new Intent(this, DisplaySelectedItem.class);
            intent.putExtra(EXTRA_MESSAGE, selectedFile);
            startActivity(intent);
        }

    }


    //Calls settings activity
    public void goSettings(View view){
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

        //Each time the user searches for something anywhere in the app, the android system automatically creates an intent and passes it to the
        //SearchActivity. Coding for this is in the IntentFilter and MetaData tag contained in the Manifest File under the SearchActivity tag
        switch(id){
            case R.id.search:
                goSearch(null);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(this, About.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
