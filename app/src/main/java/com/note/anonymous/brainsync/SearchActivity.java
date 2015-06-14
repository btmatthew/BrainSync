package com.note.anonymous.brainsync;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class SearchActivity extends ListActivity{
    private String query;
    private ArrayList<String> selectedMenuItems = new ArrayList<>();
    private int itemSelectedCount;
    private String directory;
    private Menu menu;
    private MenuItem item;
    private CustomAdapter dataAdapter=null;
    private ArrayList<Filenames> fileNamesList;
    private DatabaseAdapter db;
    final static private String SORT_NAME_SEARCH="sortMethod";
    final static private String SORT_METHOD_SEARCH = null;
    private static final int SUBMENU1 = 3;
    private static final int SUBMENU2 = 4;
    private static final int SUBMENU3 = 5;
    private static final int GROUP1 = 6;
    private int sortingMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getActionBar();

        Intent intent = getIntent();
        directory=getString(R.string.directoryLocation);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //Get the search query and assign it to string query.
            query = intent.getStringExtra(SearchManager.QUERY).trim();
            actionBar.setTitle("Search Results for '"+query+"'");
            actionBar.show();
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
        SharedPreferences getSorting = getSharedPreferences(SORT_NAME_SEARCH, 0);
        sortingMethod = getSorting.getInt(SORT_METHOD_SEARCH, 0);
        db = new DatabaseAdapter(this);

        if(db.getNumberOfRows()==0){
            Toast.makeText(this, "No Entries Yet", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }else{
            fileNamesList = db.searchByPartOfTitle(requestedEntry);
            if(!fileNamesList.isEmpty()){
                switch(sortingMethod){
                    case 0:
                        Collections.sort(fileNamesList, new Sorting.CustomComparatorByTitle());
                        break;
                    case 1:
                        Collections.sort(fileNamesList, new Sorting.CustomComparatorByDateEditedYoungestToOldest());
                        break;
                    case 2:
                        Collections.sort(fileNamesList, new Sorting.CustomComparatorByDateCreatedYoungestToOldest());
                        break;
                }
                dataAdapter= new CustomAdapter(this,R.layout.list_entries_row,fileNamesList);
                ListView listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(dataAdapter);
            }else{
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
        actionBar.setTitle("Search Results for "+query);
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
                    actionBar.setTitle("Search Results for "+query);
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
                    actionBar.setTitle("Search Results for "+query);
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
            String fileName = selectedMenuItems.get(i);
            db.deleteEntry(fileName);
            File dir = new File(directory + fileName);
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
        SubMenu subMenu = menu.addSubMenu(0,Menu.NONE,1,"Sort by").setIcon(R.drawable.ic_action_sort_by_size);

        subMenu.add(GROUP1,SUBMENU1,1,"A-Z");
        subMenu.add(GROUP1, SUBMENU2, 2, "Edit date");
        subMenu.add(GROUP1,SUBMENU3,3,"Creation date");
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
            case SUBMENU1:
                setSorting(0);
                break;
            case SUBMENU2:
                setSorting(1);
                break;
            case SUBMENU3:
                setSorting(2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setSorting(int sort){
        SharedPreferences setSorting = getSharedPreferences(SORT_NAME_SEARCH, 0);
        SharedPreferences.Editor editor= setSorting.edit();
        editor.putInt(SORT_METHOD_SEARCH,sort);
        editor.apply();
        onRestart();
    }



}
