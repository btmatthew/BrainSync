package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Settings extends Activity {

    final String AppPrefs = "AppPrefs";
    String size = "sizeKey";
    String style = "styleKey";
    String notify = "notifyKey";
    int notifvalue;
    SharedPreferences sharedpreferences;
    int change;
    int selectedstyle;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAdView = (AdView) findViewById(R.id.adView);
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
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    protected void backup() {
        Intent intent = new Intent(this, DropboxBackup.class);
        startActivity(intent);
    }

    protected void importDropbox() {
        Intent intent = new Intent(this, DropboxImport.class);
        startActivity(intent);
    }

    public void reportProblem(View view) {
        Intent intent = new Intent(this, Feedback.class);
        startActivity(intent);

    }

    public void aboutBrainSync(View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void setPreferences(View view) {
        sharedpreferences = getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
        final SeekBar seekbar = new SeekBar(this);
        seekbar.setMax(50);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Preferences");

        LinearLayout linearlayout = new LinearLayout(this);

        linearlayout.setOrientation(LinearLayout.VERTICAL);
        final TextView sampletext = new TextView(this);
        sampletext.setText("Text Preview");
        sampletext.setTextSize(sharedpreferences.getInt(size, 20));
        sampletext.setGravity(Gravity.CENTER);
        sampletext.setPadding(0, 0, 0, 30);
        sampletext.setHeight(200);

        final TextView slidertext = new TextView(this);
        slidertext.setText("Move slider to adjust text size (Minimum is 10)");

        final TextView textstyle = new TextView(this);
        textstyle.setText("Select Text Style");
        textstyle.setTypeface(null, Typeface.BOLD);
        //textstyle.setTextSize(20);

        final TextView notification = new TextView(this);
        notification.setText("Push Settings");
        //notification.setTextSize(20);
        notification.setPadding(0, 20, 0, 0);
        notification.setTypeface(null, Typeface.BOLD);

        final TextView soundtext = new TextView(this);
        soundtext.setText("Play Notification Sound");
        // soundtext.setTextSize(10);

        final CheckBox soundcheck = new CheckBox(this);


        View line = new View(this);
        line.setBackgroundColor(getResources().getColor(R.color.mygrey));
        final int a = view.getWidth();
        line.setLayoutParams(new LinearLayout.LayoutParams(a, 5));

        View line1 = new View(this);
        line1.setBackgroundColor(getResources().getColor(R.color.mygrey));
        line1.setLayoutParams(new LinearLayout.LayoutParams(a, 5));

        View line2 = new View(this);
        line2.setBackgroundColor(getResources().getColor(R.color.mygrey));
        line2.setLayoutParams(new LinearLayout.LayoutParams(a, 5));

        LinearLayout linearlayout1 = new LinearLayout(this);
        LinearLayout seekbarlayout = new LinearLayout(this);
        LinearLayout notificationlayout = new LinearLayout(this);
        notificationlayout.setPadding(0, 0, 0, 30);

        notificationlayout.setOrientation(LinearLayout.HORIZONTAL);
        notificationlayout.addView(soundtext);
        soundtext.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));
        notificationlayout.addView(soundcheck);


        seekbarlayout.setOrientation(LinearLayout.HORIZONTAL);
        seekbarlayout.addView(seekbar);
        seekbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT));

        final TextView textsize = new TextView(this);
        textsize.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textsize.setMaxLines(1);
        seekbarlayout.addView(textsize);

        final RadioGroup buttonstyle = new RadioGroup(this);
        RadioButton normal = new RadioButton(this);
        normal.setText("No Style");
        normal.setId(R.id.normal_button);
        RadioButton bold = new RadioButton(this);
        bold.setText("Bold");
        bold.setId(R.id.bold_button);
        RadioButton italic = new RadioButton(this);
        italic.setText("Italic");
        italic.setId(R.id.italic_button);
        RadioButton bolditalic = new RadioButton(this);
        bolditalic.setText("Bold Italic");
        bolditalic.setId(R.id.bold_italic_button);
        buttonstyle.addView(normal);
        buttonstyle.addView(bold);
        buttonstyle.addView(italic);
        buttonstyle.addView(bolditalic);
        buttonstyle.setOrientation(LinearLayout.HORIZONTAL);
        buttonstyle.setGravity(Gravity.CENTER);
        linearlayout1.addView(buttonstyle);

        linearlayout.addView(sampletext);
        linearlayout.addView(slidertext);
        linearlayout.addView(line);
        linearlayout.addView(seekbarlayout);
        linearlayout.addView(textstyle);
        linearlayout.addView(line1);
        linearlayout.addView(linearlayout1);
        linearlayout.addView(notification);
        linearlayout.addView(line2);
        linearlayout.addView(notificationlayout);


        dialog.setView(linearlayout);

        seekbar.setProgress(sharedpreferences.getInt(size, 20));
        textsize.setText(String.valueOf(sharedpreferences.getInt(size, 20)));
        change = sharedpreferences.getInt(size, 20);
        int w = sharedpreferences.getInt(style, 1);
        switch (w) {
            case 1:
                normal.setChecked(true);
                selectedstyle = 1;
                sampletext.setTypeface(null, Typeface.NORMAL);
                break;
            case 2:
                bold.setChecked(true);
                selectedstyle = 2;
                sampletext.setTypeface(null, Typeface.BOLD);
                break;
            case 3:
                italic.setChecked(true);
                selectedstyle = 3;
                sampletext.setTypeface(null, Typeface.ITALIC);
                break;
            case 4:
                bolditalic.setChecked(true);
                selectedstyle = 4;
                sampletext.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
        }

        int check = sharedpreferences.getInt(notify, 1);

        if (check == 0) {
            soundcheck.setChecked(false);
            Log.d("TAG", "Num 1");
        } else {
            soundcheck.setChecked(true);
            Log.d("TAG", "Num 2");
        }

        Log.d("TAG", "Check = " + String.valueOf(check));

//        switch (notifvalue) {
//            case 0:
//                soundcheck.setChecked(false);
//                Log.d("TAG", "Num 1");
//                break;
//            case 1:
//                soundcheck.setChecked(true);
//                Log.d("TAG", "Num 2");
//                break;
//        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sampletext.setTextSize(progress);
                textsize.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() < 10) {
                    int diff = 10 - seekBar.getProgress();
                    seekBar.setProgress(diff + seekBar.getProgress());
                    change = 10;
                    textsize.setText(String.valueOf(change));
                } else {

                    change = seekBar.getProgress();
                }
            }
        });

        buttonstyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {
                    case R.id.normal_button:
                        sampletext.setTypeface(null, Typeface.NORMAL);
                        selectedstyle = 1;
                        break;
                    case R.id.bold_button:
                        sampletext.setTypeface(null, Typeface.BOLD);
                        selectedstyle = 2;
                        break;
                    case R.id.italic_button:
                        sampletext.setTypeface(null, Typeface.ITALIC);
                        selectedstyle = 3;
                        break;
                    case R.id.bold_italic_button:
                        sampletext.setTypeface(null, Typeface.BOLD_ITALIC);
                        selectedstyle = 4;
                        break;
                }

            }
        });

        soundcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifvalue = 1;
                    Log.d("TAG", "Listener Num 1");
                } else {
                    notifvalue = 0;
                    Log.d("TAG", "Listener Num 2");
                }
            }
        });


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                if (change < 10) {
                    //Don't write it
                } else {
                    editor.putInt(size, change);
                }
                editor.putInt(style, selectedstyle);
                editor.putInt(notify, notifvalue);
                editor.apply();

            }
        });

        dialog.setNegativeButton("Cancel", null);

        dialog.show();

    }

}