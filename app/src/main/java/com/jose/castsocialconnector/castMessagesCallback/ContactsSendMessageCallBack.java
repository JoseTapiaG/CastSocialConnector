package com.jose.castsocialconnector.castMessagesCallback;

import android.app.Activity;
import android.app.FragmentTransaction;

import com.google.android.gms.cast.CastDevice;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.sendMessage.CreateMessageFragment;

public class ContactsSendMessageCallBack extends BaseCastMessageCallback {

    public ContactsSendMessageCallBack(Activity activity) {
        super(activity);
    }

    @Override
    public String getNamespace() {
        return activity.getString(R.string.send_message_select_contact_namespace);
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        CreateMessageFragment newFragment = new CreateMessageFragment();
        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }
}
