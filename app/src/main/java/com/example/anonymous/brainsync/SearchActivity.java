package com.example.anonymous.brainsync;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
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

        //Gets the intent from the activity that's calling this one
        Intent intent = getIntent();
        //Verify the action
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Get the search query and assign it to string query.
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            //Pass query to search method
            carryOutSearch(query);
           // Toast.makeText(this, "You searched for "+query, Toast.LENGTH_LONG).show();

        }
    }

    ListView result;
    public void carryOutSearch(String query) {
        int i;
        //Create File object and pass to it the directory where all our files are stored
        File sQuery = new File("data/data/com.example.anonymous.brainsync/files");
        //List all the files in that directory and passes it to availableFiles array
        File[] availableFiles = sQuery.listFiles();
        //Gets the size of the array
        String[] a = new String[availableFiles.length];
        //For loop based on the size of the array gets the name of all files in the directory
        for (i = 0; i < a.length; i++) {
            a[i] = availableFiles[i].getName();
        }
        try {
            //Checks array a for query
            if (Arrays.asList(a).contains(query)) {
                //if condition is matched, create a new array and pass it the value of value
                String[] newArray = {query};
                //create a new ArrayAdapter and pass our newly created array to it
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newArray);
                //Use the standard android list view and pass it's properties to our ListView 'result' created above
                result = (ListView) findViewById(android.R.id.list);
                //Put the contents of the adapter (which is content in the array 'newArray') into ListView
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

    //This method is called when the search result is successful and the user selects the listed item
    //Same procedure used in the ListEntriesActivity to display content of a selected file is re-used here
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

    //Adds the search menu so user can search still carry out search without going back
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

        //If the selected menu item is search launch the search bar at the top of the screen. See this section in MainActivity for more explanation
        if (id == R.id.search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
