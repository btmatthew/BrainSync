package com.note.anonymous.brainsync;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "notes.db";
    public static final String TABLE_NAME = "notesList";
    public static final String COLUMN1="noteTitle";
    public static final String COLUMN2="creationDate";
    public static final String COLUMN3="editDate";
    public static final String COLUMN4="fileName";
    public static final String COLUMN5="fileType";
    public static final String TEXT="TEXT";
    public static final String INTEGER="INTEGER";
    public static final String COM=",";

    public DatabaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN1 + " " + TEXT + " PRIMARY KEY " + COM +
                        COLUMN2 + " " + INTEGER + COM +
                        COLUMN3 + " " + INTEGER + COM +
                        COLUMN5 + " " + TEXT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(db);
    }
    public void addEntry(Filenames filenames){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN1,filenames.getFilename());
        contentValues.put(COLUMN2,filenames.getCreationDate());
        contentValues.put(COLUMN3,0);
        contentValues.put(COLUMN5, filenames.getFileType());
        db.insert(TABLE_NAME, null, contentValues);
    }
    //Access the cursor with cursor.moveToFirst();
    public Cursor getData(String noteTitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where noteTitle='"+noteTitle+"'",null);
        return cursor;
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
    public void updateEditDate(Filenames filenames){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN3,filenames.getEditedDate());
        db.update(TABLE_NAME,contentValues,COLUMN3+"=?",new String[]{filenames.getFilename()});
    }
    public int getNumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME);
    }
    public boolean searchByTitle(String noteTitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where noteTitle='"+noteTitle+"'",null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public void deleteEntry(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"noteTitle = ? ",new String[]{title});
    }
    public Cursor searchByPartOfTitle(String noteTitle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where noteTitle like "+"'%"+noteTitle+"%'",null);
        return cursor;
    }
    public void updateTitle(Filenames newTitle, String oldTitle){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN1,newTitle.getFilename());
        db.update(TABLE_NAME, contentValues, COLUMN1 + "=?", new String[]{oldTitle});
    }

}