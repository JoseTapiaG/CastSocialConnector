package com.jose.castsocialconnector.instagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.xml.XmlContact;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dmunoz on 28-08-15.
 */
public class InstagramApi {

    private static final String TAG = "InstagramApi";

    private final int SEARCH_SELF = 0;
    private final int GET_NEW_PHOTOS = 1;
    private final int GET_USER_ID_TO_GET_PHOTOS = 2;
    private final int GET_USER_PHOTOS = 3;
    private final int GET_FOLLOWING_INFO = 4;

    private static final String api = "https://api.instagram.com/v1/";
    private MainActivity activity;
    private Context context;

    // SharedPreferences
    private SharedPreferences settings;

    public InstagramApi(MainActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        settings = PreferenceManager
                .getDefaultSharedPreferences(this.context);
    }

    // source from http://hmkcode.com/android-parsing-json-data/
    private static String makeGetApiRequest(String urlStr) {
        InputStream inputStream;
        String result = "";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private String searchedUsername;

        private int type;

        public HttpAsyncTask(int type) {
            this.type = type;
        }

        public HttpAsyncTask(int type, String searchedUsername) {
            this.type = type;
            this.searchedUsername = searchedUsername;
        }

        @Override
        protected String doInBackground(String... params) {
            return makeGetApiRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            switch (type) {
                case SEARCH_SELF:
                    processSearchSelf(result);
                    break;
                case GET_USER_ID_TO_GET_PHOTOS:
                    processGetUserIdToGetPhotos(result, searchedUsername);
                    break;
                case GET_FOLLOWING_INFO:
                    processGetFollowingInfo(result);
                    break;
            }
        }
    }

    public void getFollowingInfo() {
        String url = api + "users/self/follows/?access_token=" + MainActivity.instagramToken;
        new HttpAsyncTask(GET_FOLLOWING_INFO).execute(url);
    }

    public void getUserPhotos(String username) {
        String url = api + "users/search?q=" + username +
                "&access_token=" + MainActivity.instagramToken;
        new HttpAsyncTask(GET_USER_ID_TO_GET_PHOTOS, username).execute(url);
    }

    public void searchSelf() {
        searchUser(SEARCH_SELF);
    }

    private void searchUser(int type) {
        String url = "https://api.instagram.com/v1/users/self/?access_token=" + MainActivity.instagramToken;
        new HttpAsyncTask(type).execute(url);
    }

    private void processGetFollowingInfo(String result) {
        Log.d(TAG, "processGetFollowingInfo");
        try {
            JSONObject json = new JSONObject(result);
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject user = data.getJSONObject(i);
                for (XmlContact contact : MainActivity.xmlContacts) {
                    if (contact.getInstagram().compareTo(user.getString("username")) == 0) {
                        contact.setInstagramUser(new InstagramUser(user));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processGetUserIdToGetPhotos(String result, String searchedUsername) {
        try {
            JSONObject json = new JSONObject(result);
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject user = data.getJSONObject(i);
                if (user.getString("username").compareTo(searchedUsername) == 0) {
                    String url = api + "users/" + user.getString("id") + "/media/recent/"
                            + "?access_token=" + MainActivity.instagramToken;
                    new HttpAsyncTask(GET_USER_PHOTOS, searchedUsername).execute(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processSearchSelf(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject user = json.getJSONObject("data");
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(this.context.getString(R.string.instagram_user_id),
                    Long.parseLong(user.getString("id")));
            editor.apply();
            activity.getUserContact().setInstagramUser(new InstagramUser(user));
            Log.d(TAG, "User found, id: " + user.getString("id"));
            long id_int = settings.getLong(
                    this.context.getString(R.string.instagram_user_id),
                    -1);
            if (id_int > 0)
                Log.d(TAG, "User found, id: " + id_int);
            else
                Log.d(TAG, "User not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
