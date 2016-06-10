package com.jose.castsocialconnector.photo;

import android.os.StrictMode;
import android.util.Log;

import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.config.Config;
import com.jose.castsocialconnector.contacts.AlbumContactsFragment;
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
        if (!Config.DEBUG) {
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
        } else
            return getDebugPhotos();
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

    @Override
    protected void onBackPressed() {
        changeFragment(new AlbumContactsFragment());
    }

    public ArrayList<PhotoJSON> getDebugPhotos() {
        ArrayList<PhotoJSON> photos = new ArrayList<>();
        contacto = ((MainActivity) getActivity()).currentContact;
        PhotoJSON photoJSON;

        if (contacto.getNickname().equals("Natalia")) {
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
            return photos;
        } else if (contacto.getNickname().equals("Javiera")) {
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
        } else if (contacto.getNickname().equals("Victor")) {
            photoJSON = new PhotoJSON();
            photoJSON.setCaption("Una rica cena familiar");
            photoJSON.setId("3");
            photoJSON.setHighResUrl("http://www.kazikes.es/wp-content/uploads/2015/12/cena-familiar4.jpg");
            photoJSON.setNickname("Victor");
            photos.add(photoJSON);
        } else if (contacto.getNickname().equals("Luis")) {
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
