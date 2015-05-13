package com.example.anonymous.brainsync;

import android.app.ListActivity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class SearchActivity extends ListActivity implements AdapterView.OnItemClickListener {
    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    String query;
    ListView result;
    ArrayList<String> selectedMenuItem = new ArrayList<String>();
    int itemSelectedCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //Gets the intent from the activity that's calling this one
        Intent intent = getIntent();
        //Verify the action

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Get the search query and assign it to string query.
            query = intent.getStringExtra(SearchManager.QUERY).trim();
            //Pass query to search method
            carryOutSearch(query);
          //  Log.d("Gency", "Search Active");
           // Toast.makeText(this, "You searched for "+query, Toast.LENGTH_LONG).show();

        }


    }


    public void carryOutSearch(String requestedEntry) {

        int i;
        //Create File object and pass to it the directory where all our files are stored
        File sQuery = new File("data/data/com.example.anonymous.brainsync/files");
        //List all the files in that directory and passes it to availableFiles array
        File[] availableFiles = sQuery.listFiles();
        //If statement prevents the app from crashing when there are no entries upon search
        if (availableFiles == null) {
            Toast.makeText(this, "No Entries Yet", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {

            //Gets the size of the array
            String[] a = new String[availableFiles.length];
            //For loop based on the size of the array gets the name of all files in the directory
            for (i = 0; i < a.length; i++) {
                a[i] = availableFiles[i].getName();
            }

            List<String> list = new ArrayList<String>(Arrays.asList(a));
            for (Iterator<String> it=list.iterator(); it.hasNext();) {
                if (!it.next().contains(requestedEntry))
                    it.remove();
            }

            if(list.size() != 0) {

            //    try {
            //Checks array a for query
            //if (Arrays.asList(a).contains(requestedEntry)) {
               //if condition is matched, create a new array and pass it the value of value
               // String[] newArray = {requestedEntry};
                //create a new ArrayAdapter and pass our newly created array to it
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
                //Use the list view under search activity XML and pass it's properties to our ListView 'result' created above
                result = (ListView) findViewById(android.R.id.list);
                //Put the contents of the adapter (which is content in the array 'newArray') into ListView
                result.setAdapter(adapter);

                result.setOnItemClickListener(this);

                result.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                result.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                        String a = result.getItemAtPosition(position).toString();

                        if (selectedMenuItem.contains(a)) {

                            //gets the position of the de-selected item and passes it to int b
                            int b = selectedMenuItem.indexOf(a);
                            //system removes element at position b in the array
                            selectedMenuItem.remove(b);
                            //size of array is gotten and passed to integer variable itemSelectedCount created earlier
                            itemSelectedCount = selectedMenuItem.size();
                            //The CAB(Contextual Action Bar) is updated to show how many items have been selected
                            mode.setTitle(itemSelectedCount + " Item(s) Selected");

                        }
                        //Else statement comes into play if the user hasn't selected the item before i.e not contained in the arraylist
                        else {
                            //The position of the selected item in the ListView is gotten and converted to string (which is the file name)
                            //and then added to the arrayList 'selectedMenuItems' created earlier using the .add method
                            //   entriesView.setBackgroundColor(Color.LTGRAY);
                            selectedMenuItem.add(result.getItemAtPosition(position).toString());

                            itemSelectedCount = selectedMenuItem.size();
                            mode.setTitle(itemSelectedCount + " Item(s) Selected");

                        }

                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.context_menu, menu);

                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                        int id = item.getItemId();
                        switch (id) {
                            case R.id.delete_menu_button:
                                deleteMethod();

                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });


            } else {
                Toast.makeText(this, requestedEntry + " does not exist", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

        }



    //    } catch(NullPointerException e) {
    //        Toast.makeText(this, "oops!", Toast.LENGTH_LONG).show();
    //    } catch(RuntimeException e) {
     //       Toast.makeText(this, "oops!", Toast.LENGTH_LONG).show();
     //   }
    }

    public void deleteMethod() {
        String itemName = "";
        //Carries out the delete action based on the size of the arraylist held by the variable itemSelectedCount
        for (int i = 0; i < itemSelectedCount; i++) {
            itemName = selectedMenuItem.get(i);
            //Gets the name of the file at position i in the array list, concatenates it with the directory assigned to the File object
            //Not sure why the concatenation works but it does... :P
            File dir = new File("data/data/com.example.anonymous.brainsync/files/" + selectedMenuItem.get(i));
            dir.delete();
        }
        Intent intentWidget= new Intent(this, ViewNotes.class);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids=AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ViewNotes.class));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intentWidget);

        selectedMenuItem.clear();
        Toast.makeText(this, itemSelectedCount+" Entries Deleted", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ListEntriesActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    //This method is called when the search result is successful and the user selects the listed item
    //Same procedure used in the ListEntriesActivity to display content of a selected file is re-used here
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String aa = result.getItemAtPosition(position).toString();

        try {
//
//            FileInputStream readingFromFile = openFileInput(aa);
//
//            int c;
//            String temp="";
//            while( (c = readingFromFile.read()) != -1){
//                temp = temp + Character.toString((char)c);
//
//            }
//            readingFromFile.close();

            Intent intent = new Intent(this, DisplaySelectedItem.class);
            intent.putExtra(EXTRA_MESSAGE, aa);
 //           intent.putExtra("body", temp);
            startActivity(intent);

        } catch (IllegalArgumentException e) {
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
