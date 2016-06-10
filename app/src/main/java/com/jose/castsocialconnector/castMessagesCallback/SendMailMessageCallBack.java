package com.jose.castsocialconnector.castMessagesCallback;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.main.MenuFragment;
import com.jose.castsocialconnector.message.NewMessagesFragment;
import com.jose.castsocialconnector.message.SendEmailThroughGmail;

public class SendMailMessageCallBack extends BaseCastMessageCallback {

    private String userEmail;
    private String oauthToken;
    private Activity activity;

    public SendMailMessageCallBack(Activity activity, String email, String oauthToken) {
        super(activity);
        this.activity = activity;
        this.userEmail = email;
        this.oauthToken = oauthToken;
    }

    @Override
    public String getNamespace() {
        return activity.getString(R.string.send_mail);
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String nameSpace, String message) {

        String[] split = message.split("__");

        if (split.length == 2 && !"".equals(split[0])) {
            new SendEmailThroughGmail((MainActivity) activity, split[0],
                   userEmail, oauthToken, split[1]).execute();
        }

        Toast.makeText(activity, "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }
}
