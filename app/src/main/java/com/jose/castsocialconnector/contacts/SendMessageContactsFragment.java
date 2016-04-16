package com.jose.castsocialconnector.contacts;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.xml.XmlContact;

import java.util.ArrayList;

public class SendMessageContactsFragment extends ContactsFragment {

    @Override
    protected String getNameSpace() {
        return getString(R.string.send_message_select_contact_namespace);
    }

    @Override
    protected ArrayList<XmlContact> getContacts() {
        return ((MainActivity) getActivity()).xmlContacts;
    }

    @Override
    public void selectContact() {
        if (((MainActivity) getActivity()).getmApiClient() != null) {
            try {
                Cast.CastApi.sendMessage(((MainActivity) getActivity()).getmApiClient(),
                        getString(R.string.send_message_select_contact_namespace), ".").setResultCallback(
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
}
