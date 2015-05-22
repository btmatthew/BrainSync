package com.note.anonymous.brainsync;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class DisplaySelectedItem extends Activity{

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE2";
    public final static String EXTRA_MESSAGE1 = "com.example.anonymous.brainsync.MESSAGE3";
    private String title;
    private String body="";
    private ShareActionProvider mShareActionProvider;
    private Intent shareItem;
    private String fileDirectory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selected_item);
        fileDirectory = getString(R.string.directoryLocation);
        final FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_edit))
                .withButtonColor(Color.GRAY)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditActiity();
            }
        });



        //Gets the intent from the activity that's calling this one (i.e either the ListEntriesActivity or the SearchActivity)
        Intent intent = getIntent();

        //Queries the activity for the data it's passing and assigns it to local string variable message
        title = intent.getStringExtra(ListEntriesActivity.EXTRA_MESSAGE);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream readingFromFile = openFileInput(title);
            int c;
            //Condition is true as long as we haven't gotten to the end of the file
            while( (c = readingFromFile.read()) != -1){
                    stringBuilder.append(Character.toString((char)c));
            }
            body=stringBuilder.toString();
            readingFromFile.close();
        }catch(IOException e){

        }
        RelativeLayout view = (RelativeLayout)findViewById(R.id.mainLayoutDisplayItem);
        TextView textView = (TextView)findViewById(R.id.noteText);

        //checks if the scrollView reached the botton of page, and hides the button
        final ScrollView scrollView =(ScrollView)findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if((scrollView.getChildAt(0).getHeight()-scrollView.getScrollY())!=scrollView.getHeight()){
                    fabButton.showFloatingActionButton();
                }else{
                    fabButton.hideFloatingActionButton();

                }
            }
        });
        //Creates a TextView component for this activity
        //Sets the data received from previous activity into the TextView
        textView.setTextSize(20);
        textView.setText(body);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callEditActiity();
                return true;
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callEditActiity();
                return true;
            }
        });

        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setTitle(title);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_selected_item, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        shareItem = new Intent(Intent.ACTION_SEND);
        shareItem.setAction(Intent.ACTION_SEND);
        shareItem.setType("text/plain");
        shareItem.putExtra(Intent.EXTRA_TEXT, body);
        mShareActionProvider.setShareIntent(shareItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //If the selected menu item is search launch the search bar at the top of the screen. See this section in MainActivity for more explanation
        switch (id) {
            case R.id.edit_menu_button:
                callEditActiity();
                break;
            case R.id.action_settings:
                Intent intent1 = new Intent(this, Settings.class);
                startActivity(intent1);
                break;
            case R.id.delete_menu_buttonSelectedItem:
                deleteNote();
                break;
            }


        return super.onOptionsItemSelected(item);
    }
    protected void callEditActiity(){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EXTRA_MESSAGE, title);
        intent.putExtra(EXTRA_MESSAGE1, body);
        startActivity(intent);
    }
    private void deleteNote(){
        File dir = new File(fileDirectory + title);
        dir.delete();
        Intent intentWidget= new Intent(this, ViewNotes.class);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids=AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ViewNotes.class));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intentWidget);
        finish();
    }
}

