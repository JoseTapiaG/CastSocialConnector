package com.jose.castsocialconnector.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jose.castsocialconnector.R;

/**
 * Created by Jose Manuel on 25/04/2016.
 */
public class ConnectWarningFragment extends Fragment {

    private MediaRouteSelector mMediaRouteSelector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_warning, container, false);

        MediaRouteButton mediaRouteButton= (MediaRouteButton) view.findViewById(R.id.media_route_button);
        mediaRouteButton.setRouteSelector(mMediaRouteSelector);

        return view;
    }

    public void setmMediaRouteSelector(MediaRouteSelector mMediaRouteSelector) {
        this.mMediaRouteSelector = mMediaRouteSelector;
    }
}
