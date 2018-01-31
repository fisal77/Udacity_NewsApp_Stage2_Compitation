package com.example.android.news.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import static com.example.android.news.DataBase.Contract.CONTENT_AUTHORITY;
import static com.example.android.news.DataBase.Contract.PATH_NEWS;

public class NewsProvider extends ContentProvider {
    public static final String LOG_TAG = NewsProvider.class.getSimpleName();
    private static final int NEWS = 100;
    private static final int NEWS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NEWS, NEWS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_NEWS + "/#", NEWS_ID);
    }
    private DbHelper dbHelper;
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                cursor = database.query(Contract.Entry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case NEWS_ID:
                selection = Contract.Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return insertNews(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertNews(Uri uri, ContentValues values) {
        String name = values.getAsString(Contract.Entry.COLUMN_WEB_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("required info");
        }
        String date = values.getAsString(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE);
        if (date == null) {
            throw new IllegalArgumentException("required info");
        }
        String auther = values.getAsString(Contract.Entry.COLUMN_AUTHOR);
        if (auther == null ) {
            throw new IllegalArgumentException(" required info");
        }
        String section = values.getAsString(Contract.Entry.COLUMN_SECTION);
        if (section == null) {
            throw new IllegalArgumentException("required info");
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(Contract.Entry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return updateNews(uri, contentValues, selection, selectionArgs);
            case NEWS_ID:
                selection = Contract.Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateNews(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateNews(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsUpdated = database.update(Contract.Entry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                rowsDeleted = database.delete(Contract.Entry.TABLE_NAME, selection, selectionArgs);
                break;
            case NEWS_ID:
                selection = Contract.Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return Contract.Entry.CONTENT_LIST_TYPE;
            case NEWS_ID:
                return Contract.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

