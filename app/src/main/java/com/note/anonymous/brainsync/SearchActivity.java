package com.note.anonymous.brainsync;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class SearchActivity extends ListActivity{
    private String query;
    private ArrayList<String> selectedMenuItems = new ArrayList<>();
    private int itemSelectedCount;
    private String directory;
    private Menu menu;
    private MenuItem item;
    private CustomAdapter dataAdapter=null;
    private ArrayList<Filenames> fileNamesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getActionBar();
        actionBar.show();
        Intent intent = getIntent();
        directory=getString(R.string.directoryLocation);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Get the search query and assign it to string query.
            query = intent.getStringExtra(SearchManager.QUERY).trim();
            //Pass query to search method
            carryOutSearch(query);
        }

        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_cancel))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    public void carryOutSearch(String requestedEntry) {

        //Create File object and pass to it the directory where all our files are stored
        File sQuery = new File(directory);
        //List all the files in that directory and passes it to availableFiles array
        File[] availableFiles = sQuery.listFiles();
        //If statement prevents the app from crashing when there are no entries upon search
        if (availableFiles == null) {
            Toast.makeText(this, "No Entries Yet", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            //Gets the size of the array
            //For loop based on the size of the array gets the name of all files in the directory
            fileNamesList = new ArrayList<>();

            for (int i = 0; i < availableFiles.length; i++) {
                String fileTitle=availableFiles[i].getName();

                if(fileTitle.toLowerCase().contains(requestedEntry)){
                    Filenames file = new Filenames(fileTitle,false);
                    if(!(file.getFilename().contains("rList")||file.getFilename().contains("share_history"))){
                        file.setFile(availableFiles[i]);
                        fileNamesList.add(file);
                    }
                }
            }
            if(fileNamesList.size() != 0) {
                dataAdapter= new CustomAdapter(this,R.layout.list_entries_row,fileNamesList);
                ListView listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(dataAdapter);
            } else {
                Toast.makeText(this, requestedEntry + " does not exist", Toast.LENGTH_LONG).show();
                onSearchRequested();
            }

        }
    }
    protected void onRestart(){
        super.onRestart();
        itemSelectedCount=0;
        selectedMenuItems.clear();
        item = menu.findItem(R.id.deleteMenuButton);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setTitle("Search Result");
        carryOutSearch(query);
    }
    private class CustomAdapter extends ArrayAdapter<Filenames>{

        private ArrayList<Filenames> fileList;
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Filenames> fileList){
            super(context, textViewResourceId,fileList);
            this.fileList= new ArrayList<>();
            this.fileList.addAll(fileList);
        }
        private class ViewHolder{
            TextView name;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_entries_row, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.listEntriesViewRow);
                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(itemSelectedCount==0){
                            openItem(holder.name.getText().toString());
                        }else{
                            selectItem(v);
                        }
                    }
                });

                holder.name.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectItem(v);
                        return true;
                    }
                });
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Filenames files = fileList.get(position);
            if(files.isSelected()){
                holder.name.setBackgroundColor(Color.GRAY);
            }else{
                holder.name.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.name.setText(files.getFilename());
            holder.name.setTag(files);
            return convertView;
        }
        private void selectItem(View v){
            item = menu.findItem(R.id.deleteMenuButton);
            android.app.ActionBar actionBar = getActionBar();

            TextView tx = (TextView) v;
            Filenames filename = (Filenames) tx.getTag();
            if(!filename.isSelected()){

                tx.setBackgroundColor(Color.GRAY);
                filename.setSelected(true);
                selectedMenuItems.add(filename.getFilename());
                itemSelectedCount = selectedMenuItems.size();
                if(itemSelectedCount==0){
                    actionBar.setTitle("Entries");
                }else if(itemSelectedCount==1){
                    actionBar.setTitle("Item Selected "+itemSelectedCount);
                    item.setVisible(true);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }else{
                    actionBar.setTitle("Items Selected "+itemSelectedCount);
                }
            }else{
                tx.setBackgroundColor(Color.TRANSPARENT);
                filename.setSelected(false);
                int b = selectedMenuItems.indexOf(filename.getFilename());
                selectedMenuItems.remove(b);
                itemSelectedCount = selectedMenuItems.size();
                if(itemSelectedCount==0){
                    actionBar.setTitle("Entries");
                    item.setVisible(false);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }else if(itemSelectedCount==1){
                    actionBar.setTitle("Item Selected "+itemSelectedCount);

                }else{
                    actionBar.setTitle("Items Selected "+itemSelectedCount);
                }
            }
        }
    }
    public void deleteMethod() {
        //Carries out the delete action based on the size of the arraylist held by the variable itemSelectedCount
        for (int i = 0; i < itemSelectedCount; i++) {
            //Gets the name of the file at position i in the array list, concatenates it with the directory assigned to the File object
            //Not sure why the concatenation works but it does... :P
            File dir = new File(directory + selectedMenuItems.get(i));
            dir.delete();
        }
        Intent intentWidget = new Intent(this, ViewNotes.class);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids=AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ViewNotes.class));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intentWidget);
        selectedMenuItems.clear();
        Toast.makeText(this, itemSelectedCount+" Entries Deleted", Toast.LENGTH_LONG).show();
        itemSelectedCount=0;
        onRestart();

    }

    //This method is called when the search result is successful and the user selects the listed item
    //Same procedure used in the ListEntriesActivity to display content of a selected file is re-used here
    private void openItem(String title){
        Intent intent = new Intent(this, DisplaySelectedItem.class);
        intent.putExtra("EXTRA_MESSAGE", title);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if(itemSelectedCount==0){
            finish();
        }else{
            onRestart();
        }


    }

    //Adds the search menu so user can search still carry out search without going back
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.search:
                onSearchRequested();
                break;
            case R.id.deleteMenuButton:
                deleteMethod();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }



}
