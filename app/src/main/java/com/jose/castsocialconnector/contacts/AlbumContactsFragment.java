package com.jose.castsocialconnector.contacts;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.photo.AlbumPhotosFragment;
import com.jose.castsocialconnector.xml.XmlContact;

import java.util.ArrayList;

public class AlbumContactsFragment extends ContactsFragment {

    private ArrayList<XmlContact> filteredContacts = new ArrayList<>();

    @Override
    protected ArrayList<XmlContact> getContacts() {
        filterContacts();
        return filteredContacts;
    }

    protected void filterContacts() {
        filteredContacts.clear();
        for (XmlContact contact: MainActivity.xmlContacts) {
            if (contact.getInstagram().compareTo("") != 0) {
                filteredContacts.add(contact);
            }
        }
    }

    @Override
    public void selectContact() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.album_select_contact_namespace), ".").setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (result.isSuccess()) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected String getNameSpace() {
        return getString(R.string.album_select_contact_namespace);
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String nameSpace, String data) {
        //contacto como argumento de nuevo fragment
        XmlContact contact = filteredContacts.get(Integer.parseInt(data));
        Bundle bundle = new Bundle();
        bundle.putSerializable("contact", contact);

        //cambio de fragment
        AlbumPhotosFragment fragment = new AlbumPhotosFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack("");
        transaction.commit();
    }
}
