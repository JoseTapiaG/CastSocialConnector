package com.jose.castsocialconnector.main;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        setTextsStyle(view);
        return view;
    }

    private void setTextsStyle(View view) {
        Typeface typeface = ((MainActivity) getActivity()).typeFaceNormal;
        ((TextView) view.findViewById(R.id.connectText1)).setTypeface(typeface);
        ((TextView) view.findViewById(R.id.connectText2)).setTypeface(typeface);
    }

    public void setmMediaRouteSelector(MediaRouteSelector mMediaRouteSelector) {
        this.mMediaRouteSelector = mMediaRouteSelector;
    }
}
