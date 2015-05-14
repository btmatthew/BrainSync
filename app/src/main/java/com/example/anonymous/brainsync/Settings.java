package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Settings extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        final Button backupDropboxButton = (Button) findViewById(R.id.dropboxBackupButton);
        backupDropboxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backup();
            }
        });
        final Button importDropboxButton = (Button) findViewById(R.id.dropboxImport);
        importDropboxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                importDropbox();
            }
        });

    }
    protected void backup(){
        Intent intent = new Intent(this, DropboxBackup.class);
        startActivity(intent);
    }
    protected void importDropbox(){
        Intent intent = new Intent(this, DropboxImport.class);
        startActivity(intent);
    }

    public void reportProblem(View view) {
        Intent intent = new Intent(this, Feedback.class);
        startActivity(intent);

    }
}