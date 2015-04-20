package com.example.anonymous.brainsync;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Matthew Bulat on 13/04/2015.
 */
public class Dropbox extends Activity {
    private String fileDirectory;
    final static private String APP_KEY = "add key";
    final static private String APP_SECRET = "add key";
    private DropboxAPI<AndroidAuthSession> mDBApi;
    CustomAdapter dataAdapter=null;
    private ArrayList<Filenames> fileNamesList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);
        fileDirectory = getString(R.string.directoryLocation);
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
                display();
                //upload();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }


    }
    protected void display(){
        File dir = new File(fileDirectory);
        File[] fileList = dir.listFiles();
        int fileListLength=fileList.length;
        fileNamesList = new ArrayList<Filenames>();

        for (int i = 0; i < fileListLength; i++) {
            Filenames file = new Filenames(fileList[i].getName(),false);
            if(!file.getFilename().contains("rList")){
                file.setFile(fileList[i]);
                fileNamesList.add(file);
            }
        }
        dataAdapter= new CustomAdapter(this,R.layout.row,fileNamesList);
        ListView listView = (ListView) findViewById(R.id.dropboxSyncList);
        listView.setAdapter(dataAdapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Filenames file = (Filenames) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + file.getFilename(),
                        Toast.LENGTH_LONG).show();
            }
        });*/


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
    }



    protected void upload(final ArrayList<File> fileList) {
        new Thread(new Runnable(){
            public void run() {
                //File dir = new File(fileDirectory);

                //File[] filelist = dir.listFiles();
                //final String[] theNamesOfFiles = new String[filelist.length];
                try {
                    for (int i = 0; i < fileList.size(); i++) {
                        FileInputStream inputStream = new FileInputStream(fileList.get(i));
                        DropboxAPI.Entry response = mDBApi.putFileOverwrite(fileList.get(i).getName(), inputStream, fileList.get(i).length(), null);//.putFile(filelist[i].getName(), inputStream ,filelist[i].length(), null, null);
                        Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                    }
                } catch (DropboxException | IOException e) {
                    Log.i("test", ""+e);
                }
            }
    }).start();
    }
}
