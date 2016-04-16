package com.jose.castsocialconnector.castMessagesCallback;

import android.app.Activity;
import android.app.Fragment;

import com.google.android.gms.cast.Cast;
import com.jose.castsocialconnector.R;

public abstract class BaseCastMessageCallback implements Cast.MessageReceivedCallback{

    protected Activity activity;

    public BaseCastMessageCallback(Activity activity) {
        this.activity = activity;
    }

    abstract String getNamespace();
}
