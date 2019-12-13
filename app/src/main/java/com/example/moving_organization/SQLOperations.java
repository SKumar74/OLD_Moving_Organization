package com.example.moving_organization;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLOperations extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MODB.db";
    public static final String BOXEDCONTENT_TABLE_NAME = "boxedContent";
    public static final String BOXEDCONTENT_COLUMN_ID = "ID";
    public static final String BOXEDCONTENT_COLUMN_DESCRIPTION = "Description";
    public static final String BOXEDCONTENT_COLUMN_STATUS = "Status";

    public SQLOperations(Context context)
    {
        super(context,DATABASE_NAME,null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase DB)
    {
        // Create table method
        DB.execSQL(
                "CREATE TABLE boxedContent" +
                        "(ID integer PRIMARY KEY, Description text, Status text)"
        );
    }

    @Override
     public void onUpgrade(SQLiteDatabase DB, int oldentry, int newentry)
    {
        DB.execSQL("DROP TABLE IF EXISTS boxedContent");
        onCreate(DB);
    }

    public boolean insertBoxedContent(String Description, String Status)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues DBentry = new ContentValues();
        DBentry.put("Description",Description);
        DBentry.put("Status",Status);
        DB.insert("boxedContent",null,DBentry);
        return true;
    }



}
