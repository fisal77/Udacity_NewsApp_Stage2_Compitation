package com.example.android.news.DataBase;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.news.R;


/**
 * Created by ebtesam on 1/11/2018 AD.
 */

public class NewsCursorAdapter extends android.widget.CursorAdapter {
    long id;
    TextView titleTextView;
    TextView sectionTextView ;
    TextView authorTextView ;
    TextView dateTextView;

    public NewsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        titleTextView = view.findViewById(R.id.textview_all_title);
        sectionTextView = view.findViewById(R.id.textview_all_section);
        authorTextView= view.findViewById(R.id.textview_all_author);
        dateTextView = view.findViewById(R.id.textview_all_date);

        int webTitleColumnIndex = cursor.getColumnIndex( Contract.Entry.COLUMN_WEB_TITLE);
        int webDateColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE);
        int sectionColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_SECTION);
        int AutherColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_AUTHOR);
        String webTitle = cursor.getString(webTitleColumnIndex);
        int webDate = cursor.getInt(webDateColumnIndex);
        int section = cursor.getInt(sectionColumnIndex);
        String auther = cursor.getString(AutherColumnIndex);
        titleTextView.setText(webTitle);
        sectionTextView.setText(section);
        authorTextView.setText(auther);
        dateTextView.setText(webDate);
    }


}