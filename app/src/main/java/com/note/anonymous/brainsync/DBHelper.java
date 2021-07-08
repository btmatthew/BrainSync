package com.note.anonymous.brainsync;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public static final String REMINDER_TABLE = "reminderList";
    public static final String ENTRY_TITLE="entryTitle";
    public static final String REMINDER_ALL_CODES="alarmCode";
    public static final String LONG="LONG";
    public static final String REMINDER_PENDING_CODE="pendingCode";
    public static final String REMINDER_NOTIFICATION_ID="notificationID";
    public static final String REMINDER_SET_TIME="setDate";
    public static final String REMINDER_SCHEDULED_TIME="scheduledDate";

    private static DBHelper mInstance = null;
    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "onCREATE CALLED");
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN1 + " " + TEXT + " PRIMARY KEY " + COM +
                        COLUMN2 + " " + INTEGER + COM +
                        COLUMN3 + " " + INTEGER + COM +
                        COLUMN5 + " " + TEXT + ")"
        );

        db.execSQL(
                "CREATE TABLE " + REMINDER_TABLE + " (" +
                        REMINDER_ALL_CODES + " " + LONG + " PRIMARY KEY " + COM + ENTRY_TITLE +
                        " " + TEXT + COM + REMINDER_SET_TIME + " " + TEXT + COM + REMINDER_SCHEDULED_TIME + " " + TEXT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("TAG", "onUPGRADE CALLED");
        try {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS" + REMINDER_TABLE);
            onCreate(db);
        } catch (SQLException a){
            a.printStackTrace();
            Log.d("TAG", "EXCEPTION CAUGHT AT onUPGRADE");
        }
    }
}
