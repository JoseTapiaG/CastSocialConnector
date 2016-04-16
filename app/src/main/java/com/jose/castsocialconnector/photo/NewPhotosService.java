package com.jose.castsocialconnector.photo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jose Manuel on 14/04/2016.
 */
public class NewPhotosService implements Cast.MessageReceivedCallback {

    private Activity activity;
    private ArrayList<PhotoJSON> newPhotos = new ArrayList<>();
    private int MAX_COUNT = 10;
    private SharedPreferences settings;

    public NewPhotosService(Activity activity) {
        this.activity = activity;
        settings = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void loadNewPhotos() {
        ArrayList<XmlContact> contacts = getFilterContacts();

        synchronized (this) {
            //Iterar sobre los contactos y obtener las fotos mas recientes por cada contacto
            ArrayList<Photo> photos = new ArrayList<>();
            for (XmlContact contact : contacts)
                photos.addAll(getContactPhotos(contact));

            //Ordenar todas las photos por fecha
            Collections.sort(photos, new Comparator<Photo>() {
                @Override
                public int compare(Photo photo, Photo photo1) {
                    return -photo.getCreationDate().compareTo(photo1.getCreationDate());
                }
            });

            //Entregar hasta 20 fotos del total
            int i = photos.size() < 20 ? photos.size() - 1 : 19;
            newPhotos = new ArrayList<>();
            while (i >= 0) {
                newPhotos.add(photos.get(i).toPhotoJSON());
                i--;
            }
        }
    }

    public ArrayList<Photo> getContactPhotos(XmlContact contact) {

        //todo buscar mejor forma de manejar asynctask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            java.net.URL url = new URL(getURL(contact));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            if (inputStream != null) {
                return processGetUserPhotos(convertInputStreamToString(inputStream), contact.getNickname());
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

    private ArrayList<Photo> processGetUserPhotos(String result, String nickname) {
        ArrayList<Photo> localPhotoList = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(result);
            JSONArray data = json.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject element = data.getJSONObject(i);
                if (element.getString("type").compareTo("image") == 0) {
                    localPhotoList.add(new Photo(element, nickname));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!settings.getString(nickname, "").equals("") && localPhotoList.size() > 0)
            localPhotoList.remove(localPhotoList.size() - 1);
        return localPhotoList;
    }


    public String getURL(XmlContact contact) {

        String lastId = settings.getString(contact.getNickname(), "");

        if (lastId.equals("")) {
            return "https://api.instagram.com/v1/users/" + contact.getInstagramUser().getId() + "/media/recent/"
                    + "?access_token=" + MainActivity.instagramToken + "&count=" + MAX_COUNT;
        } else {
            return "https://api.instagram.com/v1/users/" + contact.getInstagramUser().getId() + "/media/recent/"
                    + "?access_token=" + MainActivity.instagramToken + "&min_id=" + lastId;
        }


    }

    private ArrayList<XmlContact> getFilterContacts() {
        ArrayList<XmlContact> contacts = new ArrayList<>();
        for (XmlContact contact : MainActivity.xmlContacts) {
            if (contact.getInstagram().compareTo("") != 0) {
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public ArrayList<PhotoJSON> getNewPhotos() {
        synchronized (this) {
            return newPhotos;
        }
    }

    public Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                loadNewPhotos();
            }
        };
    }

    @Override
    public synchronized void onMessageReceived(CastDevice castDevice, String namespace,
                                               String message) {
        if (activity.getString(R.string.photo_seen_namespace).equals(namespace)) {
            String[] split = message.split("__");
            String nickName = split[0];
            String id = split[1];
            settings.edit().putString(nickName, id).apply();

            for (PhotoJSON photoJSON : newPhotos)
                if (photoJSON.getId().equals(split[1])) {
                    newPhotos.remove(photoJSON);
                    break;
                }
        }

    }
}
