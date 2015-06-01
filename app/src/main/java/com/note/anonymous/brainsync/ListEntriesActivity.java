package com.note.anonymous.brainsync;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListEntriesActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE";
    public final static String EXTRA_MESSAGE1 = "com.example.anonymous.brainsync.MESSAGE1";
    private String title;
    private String body="";
    private String fileDirectory;
    //These are all created here so I could use them in multiple methods in this activity
    int itemSelectedCount = 0;
    ArrayList<String> selectedMenuItems = new ArrayList<>();
    CustomAdapter dataAdapter=null;
    private ArrayList<Filenames> fileNamesList;
    private Menu menu;
    private MenuItem item;
    private MenuItem item1;
    private DatabaseAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);


        fileDirectory = getString(R.string.directoryLocation);
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_add))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

          fabButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(ListEntriesActivity.this, AddEntryActivity.class);
                  startActivity(intent);
              }
          });

        createList();

         }

    protected void onRestart(){
        super.onRestart();
        itemSelectedCount=0;
        selectedMenuItems.clear();
        item = menu.findItem(R.id.deleteMenuButton);
        item1 = menu.findItem(R.id.search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setTitle("Entries");
        createList();
    }

    private void createList(){
        db = new DatabaseAdapter(this);
        Cursor cursor = db.getAllData();
        fileNamesList = new ArrayList<>();
        for (int i = 0; i < db.getNumberOfRows(); i++) {
            cursor.moveToNext();
            String fileName = cursor.getString(0);
            Filenames file = new Filenames();
            file.setFilename(fileName);
            file.setSelected(false);
            file.setFile(new File(fileName));
            fileNamesList.add(file);

        }

        Collections.sort(fileNamesList, new CustomComparator());
        dataAdapter= new CustomAdapter(this,R.layout.list_entries_row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.listEntriesView);
        listView.setAdapter(dataAdapter);
    }
    //Method used for purpose of sorting the ArrayList which holds Filename object
    public class CustomComparator implements Comparator<Filenames>{

        @Override
        public int compare(Filenames lhs, Filenames rhs) {
            return (lhs).getFilename().compareTo(rhs.getFilename());
        }
    }
    public void deleteMethod() {
        //Carries out the delete action based on the size of the arraylist held by the variable itemSelectedCount
        for (int i = 0; i < itemSelectedCount; i++) {
            String fileName = selectedMenuItems.get(i);
            //Removes the file from Database
            db.deleteEntry(fileName);
            //Gets the name of the file at position i in the array list, concatenates it with the directory assigned to the File object
            File dir = new File(fileDirectory + fileName);
            dir.delete();
        }


        Toast.makeText(this, itemSelectedCount+" Item(s) Deleted", Toast.LENGTH_LONG).show();
        //Cleaning up action bar

        //Sends broadcast to widget to update the viewList
        Intent intentWidget= new Intent(this, ViewNotes.class);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids=AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ViewNotes.class));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intentWidget);
        onRestart();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return super.onContextItemSelected(item);
    }

    //Inflates the action bar menu when the ListEntriesActivity starts
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_entries, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setVisible(true);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        this.menu=menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //If the selected menu item is search launch the search bar at the top of the screen. See this section in MainActivity for more explanation
        switch(id){
            case R.id.search:
                onSearchRequested();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent1 = new Intent(this, About.class);
                startActivity(intent1);
                break;
            case R.id.deleteMenuButton:
                deleteMethod();
                break;
            case R.id.editMenuButton:
                callEditActivity();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(itemSelectedCount==0){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }else{
            onRestart();
        }


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
            item1 = menu.findItem(R.id.search);
            //--Playing around :P
            // MenuItem item2 = menu.findItem(R.id.editMenuButton);
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
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }else if(itemSelectedCount==1){
                    actionBar.setTitle("Item Selected "+itemSelectedCount);
                    item.setVisible(true);
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }else{
                    actionBar.setTitle("Items Selected "+itemSelectedCount);
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
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
                    item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }else if(itemSelectedCount==1){
                    actionBar.setTitle("Item Selected "+itemSelectedCount);
                }else{
                    actionBar.setTitle("Items Selected "+itemSelectedCount);
                }
            }
        }
    }

    protected void callEditActivity(){
        title = selectedMenuItems.get(0);
        try {
            FileInputStream readingFromFile = openFileInput(title);
            int c;
            //Condition is true as long as we haven't gotten to the end of the file
            while( (c = readingFromFile.read()) != -1){

                body = body + Character.toString((char)c);
            }
            readingFromFile.close();
        }catch(IOException e){

        }
        itemSelectedCount=0;
        selectedMenuItems.clear();
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, title);
        intent.putExtra(EXTRA_MESSAGE1, body);
        startActivity(intent);

    }

    private void openItem(String title){
        Intent intent = new Intent(this, DisplaySelectedItem.class);
        intent.putExtra("EXTRA_MESSAGE", title);
        startActivity(intent);
    }
}
