package com.example.anonymous.brainsync;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Matthew Bulat on 13/04/2015.
 */
public class Dropbox extends Activity {

    final static private String APP_KEY = "ADD YOU KEY";
    final static private String APP_SECRET = "ADD YOUR KEY";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(Dropbox.this);
    }
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {

            try {
                // Required to complete auth, sets the access token on the session

                             mDBApi.getSession().finishAuthentication();

                mDBApi.getSession().finishAuthentication();


                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                upload();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }


    }
    protected void upload() {
        new Thread(new Runnable(){
            public void run() {
                File dir = new File("data/data/com.example.anonymous.brainsync/files");
                File[] filelist = dir.listFiles();
                final String[] theNamesOfFiles = new String[filelist.length];
                Log.i("test", "test1");
                try {
                    Log.i("test", "test2");
                    for (int i = 0; i < filelist.length; i++) {
                        Log.i("test", "test3");
                        FileInputStream inputStream = new FileInputStream(filelist[i]);
                        DropboxAPI.Entry response = mDBApi.putFile(filelist[i].getName(), inputStream ,filelist[i].length(), null, null);
                        Log.i("test", "test5");


                        Log.i("test", "test5");

                        Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                    }
                } catch (DropboxException | IOException e) {
                    Log.i("test", ""+e);
                }
            }
    }).start();
    }

}
