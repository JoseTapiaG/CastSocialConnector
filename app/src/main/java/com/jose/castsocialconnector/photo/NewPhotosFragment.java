package com.jose.castsocialconnector.photo;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
 * Created by Jose Manuel on 11/04/2016.
 */
public class NewPhotosFragment extends PhotosFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        changePage();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getNameSpace() {
        return getString(R.string.album_photo_namespace);
    }

    @Override
    protected ArrayList<PhotoJSON> getPhotos() {
        ArrayList<PhotoJSON> photos = ((MainActivity) getActivity()).getNewPhotosService().getNewPhotos();

        if(photos.size() == 0) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((MainActivity) getActivity()).getNewPhotosService().loadNewPhotos();
            photos = ((MainActivity) getActivity()).getNewPhotosService().getNewPhotos();
        }

        ArrayList<PhotoJSON> notSeenPhotos = new ArrayList<>();
        for(PhotoJSON photoJSON : photos) {
            if(!photoJSON.isSeen())
                notSeenPhotos.add(photoJSON);
        }

        return notSeenPhotos;
    }

    private void changePage() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.change_to_album_photo_namespace), ".").setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }
}
