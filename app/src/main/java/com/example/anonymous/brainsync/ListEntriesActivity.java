package com.example.anonymous.brainsync;

import android.app.Activity;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ListEntriesActivity extends Activity implements AdapterView.OnItemClickListener {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";

    //These are all created here so I could use them in multiple methods in this activity
    ListView entriesView;
    int itemSelectedCount = 0;
    ArrayList<String> selectedMenuItems = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);

        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Create a new file object and pass it the directory we want to list files from
        File dir = new File("data/data/com.example.anonymous.brainsync/files");
        //List all files in the directory and pass them to the array 'filelist' of File type
        File[] filelist = dir.listFiles();

        //filelist.lenght returns a value of number of files in the directory and passed to array theNamesOfFiles
        final String[] theNamesOfFiles = new String[filelist.length];
        //Loop to get name of each file and pass them to the array at different positions
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();

        }

        //ArrayAdapter that links each files in our array to the ListView used in the activity
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);

        //entriesView is a ListView created earlier as a global component to use in multiple methods
        //Getting our ListView for this activity created in XML
        entriesView = (ListView) findViewById(R.id.listEntriesView);
        //Puts the content of our array 'theNamesOfFiles' into the ListView. The adapter is just the bridge between the data and the view
        //At this point files in the array theNamesOfFlies will display on the ListView when the activity is launched
        entriesView.setAdapter(adapter);

        entriesView.setOnItemClickListener(this);

        //Enabling multiple items selection on the ListView so user perform batch deletion of files
        //http://developer.android.com/guide/topics/ui/menus.html#CAB
        entriesView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        entriesView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                //Inflates the menu with menu items in the context_menu XML file
                //Note that this menu only pops up when the user longclicks an item in the list and covers the action bar temporarily
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);

                return true;
            }

            @Override
            //This method is invoked when the user longclicks an item and also invoked each time the user clicks on other items during
            //multiple item selection
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                //The position of the selected item in the ListView is gotten and converted to string (which is the file name)
                String a = entriesView.getItemAtPosition(position).toString();
                //This if statement is necessary because should the user de-selects an item, the system should remove it from the arraylist so
                //they don't get passed to the delete method
                if (selectedMenuItems.contains(a)) {
                    //gets the position of the de-selected item and passes it to int b
                    int b = selectedMenuItems.indexOf(a);
                    //system removes element at position b in the array
                    selectedMenuItems.remove(b);
                    //size of array is gotten and passed to integer variable itemSelectedCount created earlier
                    itemSelectedCount = selectedMenuItems.size();
                    //The CAB(Contextual Action Bar) is updated to show how many items have been selected
                    mode.setTitle(itemSelectedCount+ " Item(s) Selected");
                }
                //Else statement comes into play if the user hasn't selected the item before i.e not contained in the arraylist
                else {
                    //The position of the selected item in the ListView is gotten and converted to string (which is the file name)
                    //and then added to the arrayList 'selectedMenuItems' created earlier using the .add method
                    selectedMenuItems.add(entriesView.getItemAtPosition(position).toString());
                    itemSelectedCount = selectedMenuItems.size();
                    mode.setTitle(itemSelectedCount+ " Item(s) Selected");
                }

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                //Gets the ID of the selected contextual menu item
                int id = item.getItemId();
                //Checks if it is the same as the ID of the delete menu item decalred in context_menu XML file
                if (id == R.id.delete_menu_button) {

                    deleteMethod();
                    //Clears the ArrayList of the selected items
                    selectedMenuItems.clear();
                    //Closes the floating action bar after deletion has occurred and action bar becomes visible
                    mode.finish();
                    return true;
                } else {

                    return false;
                }
            }



            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }



            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //Clear the arraylist if the user deselects all selected item, presses the back button
                //or the check icon at the top left
              selectedMenuItems.clear();

            }
        });



    }

    public void deleteMethod() {

        //Carries out the delete action based on the size of the arraylist held by the variable itemSelectedCount
        for (int i = 0; i < itemSelectedCount; i++) {

            //Gets the name of the file at position i in the array list, concatenates it with the directory assigned to the File object
            //Not sure why the concatenation works but it does... :P
            File dir = new File("data/data/com.example.anonymous.brainsync/files/" + selectedMenuItems.get(i));
            dir.delete();
        }

    }

    //This method is invoked when the user clicks on an items in the list of entries (Not longclick)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //position of the selected item in the ListView is gotten, converted to string and passed to aa
        String aa = entriesView.getItemAtPosition(position).toString();

        try {

            FileInputStream readingFromFile = openFileInput(aa);
            int c;
            String temp="";
            //Condition is true as long as we haven't gotten to the end of the file
            while( (c = readingFromFile.read()) != -1){
                temp = temp + Character.toString((char)c);

                }
            readingFromFile.close();

            //This intent starts the DisplaySelectedItem activity and passes the data we got from the file stored in temp
            Intent intent = new Intent(this, DisplaySelectedItem.class);
            intent.putExtra(EXTRA_MESSAGE, temp);
            startActivity(intent);

        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(this, "Something Is Not Right!" + " / " + aa , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    //Inflates the action bar menu when the ListEntriesActivity starts
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_entries, menu);
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
