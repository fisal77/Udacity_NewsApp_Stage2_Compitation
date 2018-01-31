package com.example.android.news.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.news.DataBase.Contract;
import com.example.android.news.DataBase.DbHelper;
import com.example.android.news.DataBase.NewsCursorAdapter;
import com.example.android.news.R;
import com.example.android.news.adapter.NewsAdapter;
import com.example.android.news.api.NewsLoader;
import com.example.android.news.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search";
    private static final int NEWS_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getName();
    private NewsAdapter newsAdapter;
    NewsCursorAdapter newsCursorAdapter;
    private TextView emptyStateTextView;
    private ProgressBar loadingIndicator;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(LOG_TAG, "onCreate()");
        ListView newsListView = findViewById(R.id.list);
        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);
        loadingIndicator = findViewById(R.id.loading_indicator);
        newsAdapter = new NewsAdapter(MainActivity.this, new ArrayList<NewsItem>());
        newsListView.setAdapter(newsAdapter);
        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem currentNewsItem = (NewsItem) parent.getItemAtPosition(position);
            if (networkInfo != null && networkInfo.isConnected()) {
                    Uri uri = Uri.parse(currentNewsItem.getWebUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
            }else {
               Toast.makeText(MainActivity.this , R.string.no_internet_connection , Toast.LENGTH_LONG).show();
            }
            }
        });
        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            DbHelper dbhelper = new DbHelper(this);
            SQLiteDatabase sqLiteDatabase = dbhelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Contract.Entry.TABLE_NAME , null);
            if (cursor.moveToFirst()){
                ArrayList<NewsItem> Items = new ArrayList<>();
                do {
                    String title = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_WEB_TITLE));
                    String url = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_WEBURL));
                    String date = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE));
                    String section = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_SECTION));
                    String author = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_AUTHOR));
                    String thumbnail = cursor.getString(cursor.getColumnIndex(Contract.Entry.COLUMN_THUMBNAIL)); // TODO - Add new image column as Blob
                    Items.add(new NewsItem(title , section , author , date , url, thumbnail));
                }while (cursor.moveToNext());
                newsAdapter.clear();
                newsAdapter.addAll(Items);
            }else {
                emptyStateTextView.setText(R.string.no_internet_connection);
            }
        }
    }
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader()");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_preference2key),
                getString(R.string.settings_preference2dafaultvalue));
        String searchFor = sharedPreferences.getString(
                getString(R.string.settings_preference1key),
                getString(R.string.settings_preference1dafaultvalue));
        // Build full Uri as http://content.guardianapis.com/search?q=nuclear&order-by=newest&show-references=author&show-tags=contributor&api-key=test&show-fields=thumbnail
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", "\"" + searchFor + "\"");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-references", "author");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        return new NewsLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {
        loadingIndicator.setVisibility(View.GONE);
        newsAdapter.clear();
        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
            Log.e(LOG_TAG, "onLoadFinished()");
        } else {
            emptyStateTextView.setText(R.string.no_news_stories_found);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        newsAdapter.clear();
        Log.e(LOG_TAG, "onLoaderReset()");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_menuitem1) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}