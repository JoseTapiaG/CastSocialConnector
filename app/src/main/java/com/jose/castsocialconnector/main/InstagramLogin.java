package com.jose.castsocialconnector.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jose.castsocialconnector.R;

/**
 *
 * Created by dmunoz on 26-08-15.
 */
public class InstagramLogin extends Activity {
    private String TAG = "InstagramLogin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String auth_token_string = settings.getString(
                getApplicationContext().getString(R.string.instagram_token),
                "");

        if (auth_token_string.compareTo("") == 0) {
            String instagramUrl = "https://instagram.com/oauth/authorize/?client_id=" +
                    getApplicationContext().getString(R.string.instagram_id) +
                    "&redirect_uri=" +
                    getApplicationContext().getString(R.string.instagram_scheme) +
                    "%3A%2F%2Foauth%2Fcallback%2Finstagram%2F&response_type=token";
            WebView mWebview  = new WebView(this);
            final Activity activity = this;
            mWebview.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    if (url.startsWith("socialconnector://")) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
            mWebview .loadUrl(instagramUrl);
            setContentView(mWebview );
        } else {
            redirectToMainActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        handleAccessToken(intent);
    }

    private void handleAccessToken(Intent intent) {
        Log.d(TAG, "handleAccessToken");
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(
                getApplicationContext().getString(R.string.instagram_scheme))) {
            String accessToken;
            if (uri.getFragment() != null) {
                accessToken = uri.getFragment().replace("access_token=", "");
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getApplicationContext().getString(R.string.instagram_token),
                        accessToken);
                editor.apply();
                Log.d(TAG, "Found access token: " + accessToken);
                redirectToMainActivity();
            } else {
                Log.d(TAG, "Access token not found. URI: " + uri.toString());
            }
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
