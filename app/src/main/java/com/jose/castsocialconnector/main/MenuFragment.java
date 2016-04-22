package com.jose.castsocialconnector.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.contacts.AlbumContactsFragment;
import com.jose.castsocialconnector.contacts.SendMessageContactsFragment;
import com.jose.castsocialconnector.photo.NewPhotosFragment;
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

        ((Button) view.findViewById(R.id.send_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new SendMessageContactsFragment());
            }
        });
        ((Button) view.findViewById(R.id.messages)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                prev();
            }
        });

        ((Button) view.findViewById(R.id.new_photos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionFragment fragment = new TransitionFragment();
                fragment.setPhotoFragment("new");
                changeFragment(fragment);
            }
        });

        ((Button) view.findViewById(R.id.album)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new AlbumContactsFragment());
            }
        });

        return view;

    }

    @Override
    protected void onBackPressed() {
        getActivity().finish();
    }
}
