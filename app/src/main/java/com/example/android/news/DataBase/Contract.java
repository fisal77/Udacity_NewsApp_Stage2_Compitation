package com.example.android.news.DataBase;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {
    public static final String PATH_NEWS = "news";
    public static final String PATH_NEWS_2 = "Favorite";
    public static final String CONTENT_AUTHORITY = "com.example.android.news";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private Contract() {
    }
    public static final class Entry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public final static String TABLE_NAME = "news";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WEB_TITLE = "webTitle";
        public final static String COLUMN_WEBURL = "webUrl";
        public final static String COLUMN_WEB_PUBLICATION_DATE = "webPublicationDate";
        public final static String COLUMN_SECTION= "sectionName";
        public final static String COLUMN_AUTHOR = "author";
        public final static String COLUMN_TYPE = "type";
    }
    public static final class Save implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS_2;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS_2;
        public final static String TABLE_NAME_ = "Favorite";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS_2);
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_WEB_TITLE = "webTitle";
        public final static String COLUMN_WEBURL = "webUrl";
        public final static String COLUMN_WEB_PUBLICATION_DATE = "webPublicationDate";
        public final static String COLUMN_SECTION= "sectionName";
        public final static String COLUMN_AUTHOR = "author";
        public final static String COLUMN_TYPE = "type";
    }
}


