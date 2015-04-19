package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;




public class Settings extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button button = (Button) findViewById(R.id.dropboxBackupButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backup(v);
            }
        });

        final Button button1 = (Button) findViewById(R.id.driveBackupButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backup1(v);
            }
        });
    }
    protected void backup(View view){
        Intent intent = new Intent(this, Dropbox.class);
        startActivity(intent);
    }

    protected void backup1(View view){
        Intent intent = new Intent(this, GoogleDrive.class);
        startActivity(intent);
    }

}
