package com.jose.castsocialconnector.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.contacts.AlbumContactsFragment;
import com.jose.castsocialconnector.contacts.SendMessageContactsFragment;
import com.jose.castsocialconnector.photo.NewPhotosFragment;
import com.jose.castsocialconnector.tutorial.MenuTutorial;
import com.jose.castsocialconnector.xml.XmlContact;

import java.util.ArrayList;

/**
 * Created by Jose Manuel on 06/04/2016.
 */
public class MenuFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.menu, container, false);

        view.findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new SendMessageContactsFragment());
            }
        });
        view.findViewById(R.id.messages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                prev();
            }
        });

        view.findViewById(R.id.new_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionFragment fragment = new TransitionFragment();
                fragment.setPhotoFragment("new");
                changeFragment(fragment);
            }
        });

        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new AlbumContactsFragment());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(settings.getBoolean("menuTutorial", true)){
            MenuTutorial menuTutorial = new MenuTutorial(this);
            menuTutorial.start();
        }

        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.home_namespace), ".").setResultCallback(
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
    protected void onBackPressed() {
        getActivity().finish();
    }
}
