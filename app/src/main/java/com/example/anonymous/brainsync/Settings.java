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

//        final Button report = (Button) findViewById(R.id.reportProblemButton);
//        report.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                reportProblem();
//            }
//        });

        /*final Button button1 = (Button) findViewById(R.id.driveBackupButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backup1(v);
            }
        });*/
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

    /*protected void backup1(View view){
        Intent intent = new Intent(this, GoogleDrive.class);
        startActivity(intent);
    }*/

}