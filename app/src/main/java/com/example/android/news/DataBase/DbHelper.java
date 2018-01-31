package com.example.android.news.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ebtesam on 1/9/2018 AD.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + Contract.Entry.TABLE_NAME + " ("
                + Contract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Entry.COLUMN_WEB_TITLE + " TEXT, "
                + Contract.Entry.COLUMN_TYPE + " TEXT,"
                + Contract.Entry.COLUMN_WEBURL + " TEXT,"
                + Contract.Entry.COLUMN_WEB_PUBLICATION_DATE + " TEXT,"
                + Contract.Entry.COLUMN_SECTION + " TEXT,"
                + Contract.Entry.COLUMN_AUTHOR + " TEXT );";
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
        String SQL_CREATE_NEWS_TABLE_2 = "CREATE TABLE " + Contract.Entry.TABLE_NAME + " ("
                + Contract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Entry.COLUMN_WEB_TITLE + " TEXT, "
                + Contract.Entry.COLUMN_TYPE + " TEXT,"
                + Contract.Entry.COLUMN_WEBURL + " TEXT,"
                + Contract.Entry.COLUMN_WEB_PUBLICATION_DATE + " TEXT,"
                + Contract.Entry.COLUMN_SECTION + " TEXT,"
                + Contract.Entry.COLUMN_AUTHOR + " TEXT );";
        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE_2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}

