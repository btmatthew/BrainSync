package com.example.anonymous.brainsync;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;


public class SearchActivity extends ListActivity implements AdapterView.OnItemClickListener {
    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       // Log.d("Gency", "Search Active");
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            carryOutSearch(query);
           // Toast.makeText(this, "You searched for "+query, Toast.LENGTH_LONG).show();

        }
    }

    ListView result;
    public void carryOutSearch(String query) {
        int i;
        File sQuery = new File("data/data/com.example.anonymous.brainsync/files");
        File[] availableFiles = sQuery.listFiles();
        String[] a = new String[availableFiles.length];
        for (i = 0; i < a.length; i++) {
            a[i] = availableFiles[i].getName();
        }
        try {
            if (Arrays.asList(a).contains(query)) {
                String[] newArray = {query};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newArray);
                result = (ListView) findViewById(android.R.id.list);
                result.setAdapter(adapter);

                result.setOnItemClickListener(this);


            }
                else{
                    Toast.makeText(this, query + " does not exist", Toast.LENGTH_LONG).show();
                }





        }  catch(RuntimeException e) {
            Toast.makeText(this, "oops!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String aa = result.getItemAtPosition(position).toString();



        try {

            FileInputStream readingFromFile = openFileInput(aa);

            int c;
            String temp="";
            while( (c = readingFromFile.read()) != -1){
                temp = temp + Character.toString((char)c);

            }


            readingFromFile.close();

            Intent intent = new Intent(this, DisplaySelectedItem.class);
            intent.putExtra(EXTRA_MESSAGE, temp);
            startActivity(intent);

        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(this, "Something Is Not Right!" + " / " + aa , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
