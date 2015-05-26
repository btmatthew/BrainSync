package com.note.anonymous.brainsync;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DropboxImport extends Activity {
    private String fileDirectory;
    final static private String PREFS_NAME="dropboxToken";
    final static private String APP_KEY = "";
    final static private String APP_SECRET = "";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private List<DropboxAPI.Entry> files;
    private ArrayList<Filenames> fileNamesList;
    CustomAdapter dataAdapter=null;
    private Thread collectFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_import);
        fileDirectory = getString(R.string.directoryLocation);
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        SharedPreferences setToken = getSharedPreferences(PREFS_NAME,0);
        String key = setToken.getString(APP_KEY, null);
        String token = setToken.getString(APP_SECRET,null);
        if(key!=null&&token!=null){
            mDBApi.getSession().setOAuth2AccessToken(token);
            startCollection();
        }else{
            mDBApi.getSession().startOAuth2Authentication(DropboxImport.this);
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
                editor.putString(APP_SECRET, accessToken);
                editor.apply();
                startCollection();

            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
    class getFileList implements Runnable{
        @Override
        public void run() {
            try {
                DropboxAPI.Entry dirent = mDBApi.metadata("/", 1000, null, true, null);

                files = dirent.contents;

            }catch(DropboxException e){
                Log.i("DropboxException", ""+e);
            }
        }
    }
    protected void startCollection(){
        collectFileName = new Thread(new getFileList());
        collectFileName.start();
        while(collectFileName.isAlive()){
            findViewById(R.id.progressBarDropboxImport).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.progressBarDropboxImport).setVisibility(View.INVISIBLE);
        if(files.isEmpty()){
            runOnUiThread(new Toasting("Your Dropbox is empty! We cannot upload anything to your Brain."));
            finish();
        }else{
            display();
        }
    }

    protected void display(){
        int fileListLength=files.size();
        fileNamesList = new ArrayList<>();
        for(int i=0; i<fileListLength;i++){
            Filenames file = new Filenames(files.get(i).fileName(),false);
            fileNamesList.add(file);
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.importListView);
        listView.setAdapter(dataAdapter);
    }


    private class CustomAdapter extends ArrayAdapter<Filenames> {
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
    public void selectAll(View v){
        ArrayList<Filenames> fileList = dataAdapter.fileList;
        for(int i=0; i<fileList.size();i++){
            fileList.get(i).setSelected(true);
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.importListView);
        listView.setAdapter(dataAdapter);
        Button selectAllButton = (Button) findViewById(R.id.buttonSelectAllImportDropbox);
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
        ListView listView = (ListView) findViewById(R.id.importListView);
        listView.setAdapter(dataAdapter);
        Button selectAllButton = (Button) findViewById(R.id.buttonSelectAllImportDropbox);
        selectAllButton.setText("Select All");
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll(v);
            }
        });
    }

    public void importFiles(View v){
        ArrayList<Filenames> fileListToPass= new ArrayList<>();
        ArrayList<Filenames> fileList = dataAdapter.fileList;
        for(int i=0; i<fileList.size();i++){
            Filenames file = fileList.get(i);
            if(file.isSelected()){
                fileListToPass.add(file);
            }
        }
        download(fileListToPass);
    }

    protected void download(final ArrayList<Filenames> fileList){
        findViewById(R.id.progressBarDropboxImport).setVisibility(View.VISIBLE);
        new Thread(new Runnable(){
            public void run() {

                try {
                    for (int i = 0; i < fileList.size(); i++) {
                        File file = new File(fileDirectory+"/"+fileList.get(i).getFilename());
                        FileOutputStream outputStream = new FileOutputStream(file);
                        DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/"+fileList.get(i).getFilename(), null, outputStream, null);
                    }
                    runOnUiThread(new Toasting("All done! Your Brain is downloaded from Dropbox!"));
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
                    toast.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 0);
                    toast.show();

                }

            });
        }
    }

}
