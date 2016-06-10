package com.jose.castsocialconnector.photo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.config.Config;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.main.MenuFragment;
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
 * Created by Jose Manuel on 11/04/2016.
 */
public class NewPhotosFragment extends PhotosFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<PhotoJSON> getPhotos() {
        if (!Config.DEBUG) {
            ArrayList<PhotoJSON> photos = ((MainActivity) getActivity()).getNewPhotosService().getNewPhotos();

            if (photos.size() == 0) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((MainActivity) getActivity()).getNewPhotosService().loadNewPhotos();
                photos = ((MainActivity) getActivity()).getNewPhotosService().getNewPhotos();
            }

            ArrayList<PhotoJSON> notSeenPhotos = new ArrayList<>();
            for (PhotoJSON photoJSON : photos) {
                if (!photoJSON.isSeen())
                    notSeenPhotos.add(photoJSON);
            }

            return notSeenPhotos;
        } else
            return getDebugPhotos();

    }

    @Override
    protected void onBackPressed() {
        changeFragment(new MenuFragment());
    }

    public ArrayList<PhotoJSON> getDebugPhotos() {
        ArrayList<PhotoJSON> photos = new ArrayList<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());


        PhotoJSON photoJSON;

        switch (settings.getString("Natalia", "")) {
            case "":
                photoJSON = new PhotoJSON();
                photoJSON.setCaption("A financiar el paseo");
                photoJSON.setId("1");
                photoJSON.setHighResUrl("http://www.infolibre.es/uploads/imagenes/bajacalidad/2013/10/07/_mcdonalds_02470215.jpg");
                photoJSON.setNickname("Natalia");
                photos.add(photoJSON);

                photoJSON = new PhotoJSON();
                photoJSON.setCaption("Que ricas vacaciones");
                photoJSON.setId("2");
                photoJSON.setHighResUrl("http://www.datoexpress.cl/wp-content/uploads/2016/01/vacaciones.jpg");
                photoJSON.setNickname("Natalia");
                photos.add(photoJSON);
                break;

            case "1":
                photoJSON = new PhotoJSON();
                photoJSON.setCaption("Que ricas vacaciones");
                photoJSON.setId("2");
                photoJSON.setHighResUrl("http://www.datoexpress.cl/wp-content/uploads/2016/01/vacaciones.jpg");
                photoJSON.setNickname("Natalia");
                photos.add(photoJSON);
                break;
        }

        switch (settings.getString("Javiera", "")) {
            case "":
                photoJSON = new PhotoJSON();
                photoJSON.setId("1");
                photoJSON.setCaption("Ultima prueba, vamos que se puede!!");
                photoJSON.setHighResUrl("http://ambitodelaeducacion.com/wp-content/uploads/2016/03/0116.jpg");
                photoJSON.setNickname("Javiera");
                photos.add(photoJSON);

                photoJSON = new PhotoJSON();
                photoJSON.setCaption("Al fin graduada");
                photoJSON.setId("2");
                photoJSON.setHighResUrl("http://memoryfilm.mx/img/graduacion1.jpg");
                photoJSON.setNickname("Javiera");
                photos.add(photoJSON);
                break;

            case "1":

                photoJSON = new PhotoJSON();
                photoJSON.setCaption("Ultima prueba, vamos que se puede!!");
                photoJSON.setId("2");
                photoJSON.setHighResUrl("http://memoryfilm.mx/img/graduacion1.jpg");
                photoJSON.setNickname("Javiera");
                photos.add(photoJSON);
                break;
        }

        if (settings.getString("Victor", "").equals("")) {
            photoJSON = new PhotoJSON();
            photoJSON.setCaption("Una rica cena familiar");
            photoJSON.setId("3");
            photoJSON.setHighResUrl("http://www.kazikes.es/wp-content/uploads/2015/12/cena-familiar4.jpg");
            photoJSON.setNickname("Victor");
            photos.add(photoJSON);
        }

        if (settings.getString("Luis", "").equals("")) {
            photoJSON = new PhotoJSON();
            photoJSON.setCaption("Muy buen concierto, feliz :D");
            photoJSON.setId("4");
            photoJSON.setHighResUrl("http://www.fmdos.cl/wp-content/uploads/2015/03/CONCIERTOS.jpg");
            photoJSON.setNickname("Luis");
            photos.add(photoJSON);
        }

        return photos;
    }
}
