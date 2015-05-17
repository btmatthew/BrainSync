package com.note.anonymous.brainsync;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String version = getVersion();

        TextView view = (TextView) findViewById(R.id.aboutVersion);
        view.setText("Version "+version);
    }

    private String getVersion(){
        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        }
        catch (  PackageManager.NameNotFoundException e) {
            Log.e("FeedBack", "Error getting version");
            return "?";
        }
    }

}
