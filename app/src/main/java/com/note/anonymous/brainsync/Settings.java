package com.note.anonymous.brainsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
    SharedPreferences sharedpreferences;
    int change;// = 20;
    int selectedstyle;

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

    public void aboutBrainSync(View view){
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void setPreferences(View view){
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
        sampletext.setPadding(0,0,0,30);

        final TextView slidertext = new TextView(this);
        slidertext.setText("Move slider to adjust text size (Minimum is 10)");

        final TextView textstyle = new TextView(this);
        textstyle.setText("Select Text Style");

        View line = new View(this);
        line.setBackgroundColor(getResources().getColor(R.color.mygrey));
        final int a = view.getWidth();
        line.setLayoutParams(new LinearLayout.LayoutParams(a, 5));

        View line1 = new View(this);
        line1.setBackgroundColor(getResources().getColor(R.color.mygrey));
        line1.setLayoutParams(new LinearLayout.LayoutParams(a, 5));

        LinearLayout linearlayout1 = new LinearLayout(this);
        LinearLayout seekbarlayout = new LinearLayout(this);
        seekbarlayout.setOrientation(LinearLayout.HORIZONTAL);
        seekbarlayout.addView(seekbar);
        seekbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT ));
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

        dialog.setView(linearlayout);

            seekbar.setProgress(sharedpreferences.getInt(size, 20));
            textsize.setText(String.valueOf(sharedpreferences.getInt(size, 20)));
            change = sharedpreferences.getInt(size, 20);
            int w = sharedpreferences.getInt(style, 1);
            switch (w){
                case 1:
                    normal.setChecked(true);
                    selectedstyle=1;
                    sampletext.setTypeface(null, Typeface.NORMAL);
                    break;
                case 2:
                    bold.setChecked(true);
                    selectedstyle=2;
                    sampletext.setTypeface(null, Typeface.BOLD);
                    break;
                case 3:
                    italic.setChecked(true);
                    selectedstyle=3;
                    sampletext.setTypeface(null, Typeface.ITALIC);
                    break;
                case 4:
                    bolditalic.setChecked(true);
                    selectedstyle=4;
                    sampletext.setTypeface(null, Typeface.BOLD_ITALIC);
                    break;
            }

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

                if(seekBar.getProgress()<10){
                    int diff = 10-seekBar.getProgress();
                    seekBar.setProgress(diff+seekBar.getProgress());
                    change = 10;
                    textsize.setText(String.valueOf(change));
                }else {

                    change = seekBar.getProgress();
                }
            }
        });

        buttonstyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId){
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


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(change<10){
                    //Don't write it
                }else {
                    editor.putInt(size, change);
                }
                editor.putInt(style, selectedstyle);
                editor.commit();

            }
        });

        dialog.setNegativeButton("Cancel", null);

        dialog.show();

    }

}