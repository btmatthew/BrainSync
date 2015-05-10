package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Matthew Bulat on 13/04/2015.
 */
public class DropboxBackup extends Activity {
    private String fileDirectory;
    final static private String PREFS_NAME="dropboxToken";
    final static private String APP_KEY = "shz2ba3aei84dxd";
    final static private String APP_SECRET = "vz5ksv0lk7xxylp";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    CustomAdapter dataAdapter=null;
    private ArrayList<Filenames> fileNamesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_backup);
        fileDirectory = getString(R.string.directoryLocation);
        File file = new File(fileDirectory);
        if(file.list().length<0){
            runOnUiThread(new Toasting("Your Brain is empty! :O , please add some notes to your Brain."));
            finish();
        }else{
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            SharedPreferences setToken = getSharedPreferences(PREFS_NAME,0);
            String key = setToken.getString(APP_KEY, null);
            String token = setToken.getString(APP_SECRET,null);
            if(key!=null&&token!=null){
                mDBApi.getSession().setOAuth2AccessToken(token);
                display();
            }else{
                mDBApi.getSession().startOAuth2Authentication(DropboxBackup.this);
            }




        }

    }
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {

            try {
                // Required to complete auth, sets the access token on the session

                mDBApi.getSession().finishAuthentication();
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                SharedPreferences setToken = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor= setToken.edit();
                editor.putString(APP_KEY,"oauth2:");
                editor.putString(APP_SECRET,accessToken);
                editor.apply();
                display();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }


    }
    protected void display(){
        File[] fileList = new File(fileDirectory).listFiles();
        fileNamesList = new ArrayList<Filenames>();

        for (int i = 0; i < fileList.length; i++) {
            Filenames file = new Filenames(fileList[i].getName(),false);
            if(!(file.getFilename().contains("rList")||file.getFilename().contains("share_history"))){
                file.setFile(fileList[i]);
                fileNamesList.add(file);
            }
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.dropboxSyncList);
        listView.setAdapter(dataAdapter);
    }
    private class CustomAdapter extends ArrayAdapter<Filenames>{
        private ArrayList<Filenames> fileList;
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Filenames> fileList){
            super(context, textViewResourceId,fileList);
            this.fileList= new ArrayList<>();
            this.fileList.addAll(fileList);
        }
        private class ViewHolder{
            TextView code;
            CheckBox name;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.row, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.fileNameDropboxSync);
                holder.name = (CheckBox) convertView.findViewById(R.id.syncDropboxCheckbox);
                convertView.setTag(holder);
                holder.name.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        CheckBox cb = (CheckBox) v ;
                        Filenames filename = (Filenames) cb.getTag();
                        filename.setSelected(cb.isChecked());
                    }
                });
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            Filenames files = fileList.get(position);
            holder.name.setText(files.getFilename());
            holder.name.setChecked(files.isSelected());
            holder.name.setTag(files);
            return convertView;
        }
    }

    public void backup(View v){
        ArrayList<File> fileListToPass= new ArrayList<File>();
        ArrayList<Filenames> fileList = dataAdapter.fileList;
        for(int i=0; i<fileList.size();i++){
            Filenames file = fileList.get(i);
            if(file.isSelected()){
                fileListToPass.add(file.getFile());
            }
        }
        upload(fileListToPass);
    }
    public void selectAll(View v){
        ArrayList<Filenames> fileList = dataAdapter.fileList;
        for(int i=0; i<fileList.size();i++){
            fileList.get(i).setSelected(true);
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.dropboxSyncList);
        listView.setAdapter(dataAdapter);
        Button selectAllButton = (Button) findViewById(R.id.buttonSelectAll);
        selectAllButton.setText("Deselect All");
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectAll(v);
            }
        });

    }
    public void deselectAll(View v){
        ArrayList<Filenames> fileList = dataAdapter.fileList;
        for(int i=0; i<fileList.size();i++){
            fileList.get(i).setSelected(false);
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.dropboxSyncList);
        listView.setAdapter(dataAdapter);
        Button selectAllButton = (Button) findViewById(R.id.buttonSelectAll);
        selectAllButton.setText("Select All");
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll(v);
            }
        });
    }


    protected void upload(final ArrayList<File> fileList) {
        findViewById(R.id.spinningCircle).setVisibility(View.VISIBLE);
        new Thread(new Runnable(){
            public void run() {

                try {
                    for (int i = 0; i < fileList.size(); i++) {
                        FileInputStream inputStream = new FileInputStream(fileList.get(i));
                        mDBApi.putFileOverwrite(fileList.get(i).getName(), inputStream, fileList.get(i).length(), null);
                    }
                    runOnUiThread(new Toasting("All done! Your Brain is uploaded to Dropbox!"));
                    finish();
                } catch (DropboxException | IOException e) {
                    Log.i("DropboxException", ""+e);
                }
            }
        }).start();

    }

    class Toasting implements Runnable{

        private String data;

        public Toasting(String x){
            this.data =x;
        }

        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Context context = getApplicationContext();
                    CharSequence text = data;
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            });
        }
    }
}
