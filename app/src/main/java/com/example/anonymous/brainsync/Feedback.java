package com.example.anonymous.brainsync;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class Feedback extends Activity {

    static final int SEND_EMAIL= 1;
    private static final int PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA = 3;
    ArrayList<Uri> uris = new ArrayList<Uri>();
    int imageSelectionCounter = 0;
    int detectSelectionCounter1 = 0;
    int detectSelectionCounter2 = 0;
    int detectSelectionCounter3 = 0;
    int index1, index2, index3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        final ImageView image1 = (ImageView) findViewById(R.id.firstimage);
        image1.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_add));
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(detectSelectionCounter1) {
                    case(1):
                        imageSelectionCounter = 0;
                        selectImage(1);
                        break;
                    default:
                        imageSelectionCounter = 0;
                        pickImage();
                        break;
                }
            }
        });

        final ImageView image2 = (ImageView) findViewById(R.id.secondimage);
        image2.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_add));
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(detectSelectionCounter2) {
                    case(2):
                        imageSelectionCounter = 1;
                        selectImage(2);
                        break;
                    default:
                        imageSelectionCounter = 1;
                        pickImage();
                        break;
                }
            }
        });

        final ImageView image3 = (ImageView) findViewById(R.id.thirdimage);
        image3.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_action_add));
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(detectSelectionCounter3) {
                case(3):
                imageSelectionCounter = 1;
                selectImage(3);
                break;
                default:
                imageSelectionCounter = 2;
                pickImage();
                break;
            }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    private int getVersion(){
        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionCode;
        }
        catch (  PackageManager.NameNotFoundException e) {
            Log.e("FeedBack", "Error getting version");
            return 0;
        }
    }

    public void sendReport(View view) {


        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        String model = Build.MODEL;
        String apiversion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;

        int version = getVersion();


        EditText edit = (EditText) findViewById(R.id.issuetext);

        String issue = edit.getText().toString().trim();
        int length = issue.length();

        if(length < 10) {
            Toast.makeText(this, "Please further describe the issue", Toast.LENGTH_LONG).show();
        } else {

            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"brain.sync@yahoo.co.uk"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Complaint about BrainSync");
            intent.putExtra(Intent.EXTRA_TEXT, issue + "\n\n" + "---Support Info---" + "\n" + date + "\nManufacturer: " +
                    manufacturer + "\nModel: " + model + "\nAndroid Version: " + apiversion +
                    "\nBrainSync Version: " + version);


            if (uris.isEmpty()) {
                //Send email without photo attached
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("plain/text");
            } else if (uris.size() == 1) {
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
                intent.setType("message/rfc822");
            } else {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("message/rfc822");
            }


            startActivityForResult(Intent.createChooser(intent, "Choose Preferred E-mail Client"), SEND_EMAIL);
        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void selectImage(final int a) {
        final CharSequence[] items = { "Remove Image", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Feedback.this);
        builder.setTitle("Edit Selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Remove Image")) {
                    switch(a){
                        case(1):
                            ImageView image1 = (ImageView) findViewById(R.id.firstimage);
                            image1.setImageResource(R.drawable.ic_action_add);
                            detectSelectionCounter1 = 0;
                            uris.set(index1, null);
                            break;
                        case(2):
                            ImageView image2 = (ImageView) findViewById(R.id.secondimage);
                            image2.setImageResource(R.drawable.ic_action_add);
                            detectSelectionCounter2 = 0;
                            uris.set(index2, null);
                            break;
                        default:
                            ImageView image3 = (ImageView) findViewById(R.id.thirdimage);
                            image3.setImageResource(R.drawable.ic_action_add);
                            detectSelectionCounter3 = 0;
                            uris.set(index3, null);
                            break;
                    }

                }  else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SEND_EMAIL) {
           uris.clear();
           Intent intent = new Intent(this, Settings.class);
           startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                if (imageSelectionCounter == 0){

                ImageView image = (ImageView) findViewById(R.id.firstimage);
                image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    uris.add(selectedImage);
                    index1 = uris.indexOf(selectedImage);
                    detectSelectionCounter1 = 1;

            } else if(imageSelectionCounter == 1) {
                    ImageView image = (ImageView) findViewById(R.id.secondimage);
                    image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    uris.add(selectedImage);
                    index2 = uris.indexOf(selectedImage);
                    detectSelectionCounter2 = 2;
                    }
                else {
                    ImageView image = (ImageView) findViewById(R.id.thirdimage);
                    image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    uris.add(selectedImage);
                    index3 = uris.indexOf(selectedImage);
                    detectSelectionCounter3 = 3;
                }
            }

        }
    }




    @Override
    public void onBackPressed() {
        uris.clear();
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }
}
