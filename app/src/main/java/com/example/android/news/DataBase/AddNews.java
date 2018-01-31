package com.example.android.news.DataBase;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.news.R;

/**
 * Created by ebtesam on 1/22/2018 AD.
 */

public class AddNews extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentInventoryUri;
    TextView titleTextView;
    TextView sectionTextView ;
    TextView authorTextView ;
    TextView dateTextView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_news);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        if (mCurrentInventoryUri == null) {
            invalidateOptionsMenu();
        }
        titleTextView = findViewById(R.id.textview_all_title);
        sectionTextView = findViewById(R.id.textview_all_section);
        authorTextView= findViewById(R.id.textview_all_author);
        dateTextView = findViewById(R.id.textview_all_date);
    }

    public void saveNews() {
        String title=titleTextView.getText().toString().trim();

       String sectin=sectionTextView.getText().toString().trim();

        String auther=authorTextView.getText().toString().trim();

        String date=dateTextView.getText().toString().trim();


        //and check if all the fields in the editor are blank
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(sectin) || TextUtils.isEmpty(auther) || TextUtils.isEmpty(date)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(Contract.Entry.COLUMN_WEB_TITLE, title);
        values.put(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE, date);
        values.put(Contract.Entry.COLUMN_SECTION, sectin);
        values.put(Contract.Entry.COLUMN_AUTHOR, auther);

        getContentResolver().insert(Contract.Entry.CONTENT_URI, values);

    }




    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Contract.Entry._ID,
                Contract.Entry.COLUMN_WEB_TITLE,
                Contract.Entry.COLUMN_WEB_PUBLICATION_DATE,
                Contract.Entry.COLUMN_SECTION,
                Contract.Entry.COLUMN_AUTHOR};
        return new CursorLoader(this, mCurrentInventoryUri,
                projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int webTitleColumnIndex = cursor.getColumnIndex( Contract.Entry.COLUMN_WEB_TITLE);
            int webDateColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE);
            int sectionColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_SECTION);
            int AutherColumnIndex = cursor.getColumnIndex(Contract.Entry.COLUMN_AUTHOR);

            // Extract out the value from the Cursor for the given column index
            String webTitle = cursor.getString(webTitleColumnIndex);
            int webDate = cursor.getInt(webDateColumnIndex);
            int section = cursor.getInt(sectionColumnIndex);
            String auther = cursor.getString(AutherColumnIndex);

            // Update the views on the screen with the values from the database
            titleTextView.setText(webTitle);
            sectionTextView.setText(section);
            authorTextView.setText(auther);
            dateTextView.setText(webDate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        titleTextView.setText("");
        sectionTextView.setText("");
        authorTextView.setText("");
        dateTextView.setText("");
    }




}


