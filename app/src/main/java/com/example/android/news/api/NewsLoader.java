package com.example.android.news.api;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.news.DataBase.Contract;
import com.example.android.news.DataBase.DbHelper;
import com.example.android.news.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {
    /**
     * Tag for log messages.
     */
    private static final String LOG_TAG = NewsLoader.class.getName();
    /**
     * Query URL.
     */
    private String url;
    static SQLiteDatabase sqlitedatabase;
    static DbHelper dbHelper;

    public NewsLoader(Context context, String url) {
        super(context);
        Log.e(LOG_TAG, "NewsLoader()");
        this.url = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.e(LOG_TAG, "onStartLoading()");
    }

    @Override
    public List<NewsItem> loadInBackground() {
        // Don't perform the request if the first URL is null.
        if (url == null) {
            return null;
        }
        Log.e(LOG_TAG, "loadInBackground()");
        dbHelper = new DbHelper(getContext());
        return fetchNewsStoriesData(url);
    }
    public static List<NewsItem> fetchNewsStoriesData(String requestUrl) {
        Log.e(LOG_TAG, "fetchNewsStoriesData()");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsItem}s.
        List<NewsItem> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsItem}s.
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news stories JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<NewsItem> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        List<NewsItem> newsList = new ArrayList<>();
        sqlitedatabase = dbHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        // Try to parse the news stories JSON result/response. If there's a problem with the way
        // the JSON is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string.
            JSONObject root = new JSONObject(newsJSON);

            // Create a JSONObject from the JSON root object.
            JSONObject response = root.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news stories).
            JSONArray resultsArray = response.getJSONArray("results");

            // For each news item in the resultsArray, create an {@link NewsItem} object.
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject newsItem = resultsArray.getJSONObject(i);
                String title = newsItem.getString("webTitle");
                value.put(Contract.Entry.COLUMN_WEB_TITLE , title);
                String section = newsItem.getString("sectionName");
                value.put(Contract.Entry.COLUMN_SECTION , section);
                String date = newsItem.getString("webPublicationDate");
                value.put(Contract.Entry.COLUMN_WEB_PUBLICATION_DATE , date);
                String webUrl = newsItem.getString("webUrl");
                JSONArray tagsArray = newsItem.getJSONArray("tags");
                String author = "";
                int numberOfContributors = tagsArray.length();
                if (numberOfContributors > 0) {
                    //Add a comma if the current contributor is not the first one AND not the last one.
                    for (int j = 0; j < numberOfContributors; j++) {
                        if (j > 0 && j < numberOfContributors - 1) {
                            author += ", ";
                        }
                        //Add an "and" if the current contributor is not the first one AND it is the last one.
                        if (j > 0 && j == numberOfContributors - 1) {
                            author += " and ";
                        }
                        //Extract the name of the contributor and add it the the author's string.
                        JSONObject tagItem = tagsArray.getJSONObject(j);
                        author += tagItem.getString("webTitle");
                    }
                }
                value.put(Contract.Entry.COLUMN_AUTHOR , author);
                // Create a new {@link NewsItem} object with the title, section, author, date,
                // and webUrl from the JSON response, and add it to the list of newsList.
                newsList.add(new NewsItem(title, section, author, date, webUrl));
                sqlitedatabase.insert(Contract.Entry.TABLE_NAME , null , value);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news stories JSON results", e);
        }

        // Return the list of news stories.
        return newsList;
    }
}
