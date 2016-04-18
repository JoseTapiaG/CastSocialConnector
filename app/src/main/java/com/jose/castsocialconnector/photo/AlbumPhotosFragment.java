package com.jose.castsocialconnector.photo;

import android.os.StrictMode;
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
import java.util.ArrayList;

/**
 * Created by Jose Manuel on 11/04/2016.
 */
public class AlbumPhotosFragment extends PhotosFragment {

    private String URL;
    XmlContact contacto;

    @Override
    protected ArrayList<PhotoJSON> getPhotos() {
        contacto = ((MainActivity) getActivity()).currentContact;

        URL = "https://api.instagram.com/v1/users/" + contacto.getInstagramUser().getId() + "/media/recent/"
                + "?access_token=" + MainActivity.instagramToken;

        //todo buscar mejor forma de manejar asynctask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            if (inputStream != null) {
                return processGetUserPhotos(convertInputStreamToString(inputStream));
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return new ArrayList<>();
    }


    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private ArrayList<PhotoJSON> processGetUserPhotos(String result) {
        ArrayList<PhotoJSON> localPhotoList = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(result);
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject element = data.getJSONObject(i);
                if (element.getString("type").compareTo("image") == 0) {
                    Photo p = new Photo(element);
                    PhotoJSON photoJSON = p.toPhotoJSON();
                    photoJSON.setNickname(contacto.getNickname());
                    localPhotoList.add(photoJSON);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localPhotoList;
    }
}
