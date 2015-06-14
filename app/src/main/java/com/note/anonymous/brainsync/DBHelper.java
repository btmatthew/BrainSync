package com.note.anonymous.brainsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matthew Bulat on 14/06/2015.
 */
public class DBHelper extends SQLiteOpenHelper{

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
    private static DBHelper mInstance = null;
    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    private DBHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
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
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }
}
