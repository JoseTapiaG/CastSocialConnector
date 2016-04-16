package com.jose.castsocialconnector.photo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.xml.XmlContact;

import java.io.IOException;
import java.util.ArrayList;

public abstract class PhotosFragment extends Fragment implements Cast.MessageReceivedCallback {

    protected View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        String json = gson.toJson(getPhotos());
        sendPhotos(json);
        try {
            Cast.CastApi.setMessageReceivedCallbacks(((MainActivity) getActivity()).getmApiClient(),
                    getNameSpace(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getNameSpace();

    protected abstract ArrayList<PhotoJSON> getPhotos();

    private void sendPhotos(String contacts) {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.send_photos), contacts).setResultCallback(
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.contacts, container, false);

        mainView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        mainView.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prev();
            }
        });
        mainView.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        return mainView;

    }

    public void next() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.next_photo_namespace), ".").setResultCallback(
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

    public void prev() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.prev_photo_namespace), ".").setResultCallback(
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

    @Override
    public void onMessageReceived(CastDevice castDevice, String s, String s1) {}
}
