package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DisplaySelectedItem extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.anonymous.brainsync.MESSAGE2";
    public final static String EXTRA_MESSAGE1 = "com.example.anonymous.brainsync.MESSAGE3";
  //  public final static String SEARCH_ITEM = "com.example.anonymous.brainsync.ITEM";
    String title;
    String body;
    String body1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Gets the intent from the activity that's calling this one (i.e either the ListEntriesActivity or the SearchActivity)
        Intent intent = getIntent();

//        Log.d("Gency", "This is it: "+intent.toString());

        //Queries the activity for the data it's passing and assigns it to local string variable message
        title = intent.getStringExtra(ListEntriesActivity.EXTRA_MESSAGE);
        body = intent.getStringExtra("body");


        //Creates a TextView component for this activity
        TextView textView = new TextView(this);
        textView.setTextSize(20);

        //Sets the data received from previous activity into the TextView
        textView.setText(body);


        //Sets the EditText view to display in this activity
        setContentView(textView);



        //Makes the view scrollable if there are too many items in the file
        textView.setMovementMethod(new ScrollingMovementMethod());
        //Make the app icon at the top left corner clickable so user can go to previous activity instead of using the back button
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_selected_item, menu);

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
        } else if (id == R.id.edit_menu_button) {

            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EXTRA_MESSAGE, title);
            intent.putExtra(EXTRA_MESSAGE1, body);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}

