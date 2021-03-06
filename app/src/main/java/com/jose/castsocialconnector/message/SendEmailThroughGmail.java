package com.jose.castsocialconnector.message;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.message.authentication.OAuth2Authenticator;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 17-01-14
 * Time: 03:59 PM
 */

public class SendEmailThroughGmail extends AsyncTask<Void, Void, Void> {

    private final String TAG = "SendEmailThroughGmail";

    private MainActivity activity;
    private String messageContent;
    private String emailFrom;
    private String oauthToken;
    private String emailTo;

    public SendEmailThroughGmail(MainActivity activity,
                                 String messageContent,
                                 String emailFrom,
                                 String oauthToken,
                                 String emailTo) {
        this.activity = activity;
        this.messageContent = messageContent;
        this.emailFrom = emailFrom;
        this.oauthToken = oauthToken;
        this.emailTo = emailTo;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");
//        activity.runOnUiThread(new Runnable() {
//            public void run() {
//                CustomToast.getCustomToast(activity, "Enviando mensaje").show();
//            }
//        });
        sendEmail();
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.d(TAG, "onPostExecute");
//        Calendar cal = Calendar.getInstance();
//        Utils.logSendMessageEvent(this.emailFrom, this.emailTo, cal.getTimeInMillis());
//        FragmentManager fragmentManager = activity.getFragmentManager();
//        Fragment currentFragment = fragmentManager.findFragmentByTag(MainActivity.FRAGMENT_TAG);
//        if (currentFragment instanceof CreateMessageFragment) {
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            SendMessageFragment sendMessageFragment = new SendMessageFragment();
//            transaction.replace(R.id.fragment_container, sendMessageFragment, MainActivity.FRAGMENT_TAG);
//            transaction.commit();
//        }
//        CustomToast.getCustomToast(activity, "Mensaje enviado").show();
    }

    private void sendEmail() {
        try {
            OAuth2Authenticator sender = new OAuth2Authenticator();
            try {
                sender.sendMail(activity.getString(R.string.send_message_subject),
                        messageContent,
                        emailFrom, oauthToken, emailTo);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
