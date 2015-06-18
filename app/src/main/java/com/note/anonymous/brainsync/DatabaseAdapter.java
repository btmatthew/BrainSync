package com.note.anonymous.brainsync;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DatabaseAdapter {
    public static final String TABLE_NAME = "notesList";
    public static final String COLUMN1 = "noteTitle";
    public static final String COLUMN2 = "creationDate";
    public static final String COLUMN3 = "editDate";
    public static final String COLUMN4 = "fileName";
    public static final String COLUMN5 = "fileType";

    public static final String REMINDER_TABLE = "reminderList";
    public static final String ENTRY_TITLE = "entryTitle";
    public static final String REMINDER_ALL_CODES = "alarmCode";
    public static final String REMINDER_SET_TIME = "setDate";
    public static final String REMINDER_SCHEDULED_TIME = "scheduledDate";

    private DBHelper dbHelper;

    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void addEntry(Filenames filenames, Context context) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN1, filenames.getFilename());
        contentValues.put(COLUMN2, filenames.getCreationDate());
        contentValues.put(COLUMN3, 0L);
        contentValues.put(COLUMN5, filenames.getFileType());
        db.insert(TABLE_NAME, null, contentValues);
        if (filenames.getReminderIndicatorValue() == 1) {
            Log.d("TAG", "Got here!");
            final String AppPrefs = "AppPrefs";
            String alarm = "alarmKey";
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
            ContentValues contentValues1 = new ContentValues();
            int alarmCode = sharedPreferences.getInt(alarm, 0);
            contentValues1.put(REMINDER_ALL_CODES, (alarmCode-1));
            contentValues1.put(ENTRY_TITLE, filenames.getFilename());
            contentValues1.put(REMINDER_SET_TIME, filenames.getReminderCreationTime());
            contentValues1.put(REMINDER_SCHEDULED_TIME, filenames.getReminderScheduledTime());
            db.insert(REMINDER_TABLE, null, contentValues1);
        }

    }

    public void addReminderEntry(Filenames filenames, Context context){
        db = dbHelper.getWritableDatabase();
        final String AppPrefs = "AppPrefs";
        String alarm = "alarmKey";
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPrefs, Context.MODE_PRIVATE);
        ContentValues contentValues1 = new ContentValues();
        int alarmCode = sharedPreferences.getInt(alarm, 0);
        contentValues1.put(REMINDER_ALL_CODES, (alarmCode-1));
        contentValues1.put(ENTRY_TITLE, filenames.getFilename());
        contentValues1.put(REMINDER_SET_TIME, filenames.getReminderCreationTime());
        contentValues1.put(REMINDER_SCHEDULED_TIME, filenames.getReminderScheduledTime());
        db.insert(REMINDER_TABLE, null, contentValues1);

    }

    public ArrayList<Filenames> getAllData() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        ArrayList<Filenames> fileNamesList = new ArrayList<>();
        for (int i = 0; i < getNumberOfRowsNotesTable(); i++) {
            Filenames file = new Filenames();
            cursor.moveToNext();
            String fileName = cursor.getString(0);
            file.setFilename(fileName);
            file.setCreationDate(Long.parseLong(cursor.getString(1)));
            file.setEditedDate(Long.parseLong(cursor.getString(2)));
            file.setSelected(false);
            file.setFile(new File(fileName));
            fileNamesList.add(file);
        }
        cursor.close();
        return fileNamesList;
    }
    //ToDO get the creation and edit dates from main table for purpose of sorting the values.
    //TODO allow user to sort the values by the date or time on which the values are set to be reminded
    public ArrayList<Filenames> getAllReminders() {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + REMINDER_TABLE, null);
        //TODO this can be used to collect data from main table
        //Cursor cursor1 = db.rawQuery("select * from " + TABLE_NAME, null);
        ArrayList<Filenames> fileNamesList = new ArrayList<>();
        for (int i = 0; i < getNumberOfRowsReminderTable(); i++) {
            cursor.moveToNext();
            String fileName = cursor.getString(1);
            Filenames file = new Filenames();
            file.setFilename(fileName);
            long code = cursor.getLong(0);
            file.setAlarmCode(code);
            fileNamesList.add(file);
        }
        cursor.close();
        return fileNamesList;
    }

    public ArrayList<Filenames> getAllDataWithFile() {
        db = dbHelper.getReadableDatabase();
        String fileDirectory = "data/data/com.example.anonymous.brainsync/files/";
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        ArrayList<Filenames> fileNamesList = new ArrayList<>();

        for (int i = 0; i < getNumberOfRowsNotesTable(); i++) {
            Filenames file = new Filenames();
            cursor.moveToNext();
            String fileName = cursor.getString(0);
            file.setFilename(fileName);
            file.setSelected(false);
            file.setFile(new File(fileDirectory + fileName));
            fileNamesList.add(file);
        }
        cursor.close();
        return fileNamesList;
    }

    public void updateEditDate(Filenames filenames) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN3, filenames.getEditedDate());
        db.update(TABLE_NAME, contentValues, COLUMN1 + "=?", new String[]{filenames.getFilename()});
    }

    public int getNumberOfRowsNotesTable() {
        db = dbHelper.getReadableDatabase();
        int size = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return size;
    }

    public int getNumberOfRowsReminderTable() {
        db = dbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, REMINDER_TABLE);
    }

    public boolean searchByTitle(String noteTitle) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where noteTitle='" + noteTitle + "'", null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void deleteEntry(String title) {
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, "noteTitle = ? ", new String[]{title});
    }

    public ArrayList<Filenames> searchByPartOfTitle(String noteTitle) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where noteTitle like " + "'%" + noteTitle + "%'", null);
        ArrayList<Filenames> fileNamesList = new ArrayList<>();
        int cursorSize = cursor.getCount();
        if (cursorSize != 0) {
            for (int i = 0; i < cursorSize; i++) {
                cursor.moveToNext();
                Filenames file = new Filenames();
                file.setFilename(cursor.getString(0));
                file.setCreationDate(Long.parseLong(cursor.getString(1)));
                file.setEditedDate(Long.parseLong(cursor.getString(2)));
                file.setSelected(false);
                fileNamesList.add(file);
            }
        }
        cursor.close();
        return fileNamesList;
    }

    public void updateTitle(Filenames newTitle, String oldTitle) {
        db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN1, newTitle.getFilename());
        contentValues.put(COLUMN3, newTitle.getEditedDate());
        db.update(TABLE_NAME, contentValues, COLUMN1 + "=?", new String[]{oldTitle});
    }

}