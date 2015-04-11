package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ListEntriesActivity extends Activity implements AdapterView.OnItemClickListener {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    ListView entriesView;
    int itemSelectedCount = 0;
    ArrayList<String> selectedMenuItems = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        File dir = new File("data/data/com.example.anonymous.brainsync/files");
        File[] filelist = dir.listFiles();


        final String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();

        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);

        entriesView = (ListView) findViewById(R.id.listEntriesView);
        entriesView.setAdapter(adapter);

        entriesView.setOnItemClickListener(this);

        entriesView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        entriesView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                itemSelectedCount = itemSelectedCount+1;
                mode.setTitle(itemSelectedCount+ " Item(s) Selected");

                selectedMenuItems.add(entriesView.getItemAtPosition(position).toString());

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.delete_menu_button) {
                 //   selectedMenuItems.toArray(receivedItems);
                //    Log.d("Gency", "Button Selected "+ selectedMenuItems.get(0));
                    deleteMethod();


                 //   selectedMenuItems.toArray();
                    selectedMenuItems.clear(); //Clears the ArrayList After Button Is Selected
                    mode.finish();      //close the floating action bar
                    return true;
                } else {

                    return false;
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
            public void onDestroyActionMode(ActionMode mode) {
                itemSelectedCount = 0;

            }
        });



    }

    public void deleteMethod() {

        for (int i = 0; i < itemSelectedCount; i++) {

            File dir = new File("data/data/com.example.anonymous.brainsync/files/" + selectedMenuItems.get(i));
            dir.delete();

        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String aa = entriesView.getItemAtPosition(position).toString();



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
        getMenuInflater().inflate(R.menu.menu_list_entries, menu);
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
