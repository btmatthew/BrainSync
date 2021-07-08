package com.note.anonymous.brainsync;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;


public class FragmentParentActivity extends ActionBarActivity {

    // Declaring Your View and Variables
    final static private String SORT_NAME="sortMethod";
    final static private String SORT_METHOD = null;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Entries", "Reminders"};
    int Numboftabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_parent);



        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });


        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


    }
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    public void setSorting(int sort){
        SharedPreferences setSorting = getSharedPreferences(SORT_NAME, 0);
        SharedPreferences.Editor editor= setSorting.edit();
        editor.putInt(SORT_METHOD,sort);
        editor.apply();
    }
    public int getSorting(){
        SharedPreferences getSorting = getSharedPreferences(SORT_NAME, 0);
        return getSorting.getInt(SORT_METHOD, 0);
    }
    public void sendBroadcastToWidget(){
        Intent intentWidget= new Intent(this, ViewNotes.class);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids=AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ViewNotes.class));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intentWidget);
    }
    public void onSearch(){
        onSearchRequested();
    }

}
