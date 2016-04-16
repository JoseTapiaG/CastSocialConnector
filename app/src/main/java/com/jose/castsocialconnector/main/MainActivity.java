/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.jose.castsocialconnector.main;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.castMessagesCallback.ContactsSendMessageCallBack;
import com.jose.castsocialconnector.instagram.InstagramApi;
import com.jose.castsocialconnector.photo.NewPhotosService;
import com.jose.castsocialconnector.xml.XmlContact;
import com.jose.castsocialconnector.xml.XmlParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Main activity to send messages to the receiver.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private ConnectionCallbacks mConnectionCallbacks;
    private ConnectionFailedListener mConnectionFailedListener;
    private HelloWorldChannel mHelloWorldChannel;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    private String mSessionId;

    // Contacts
    public static ArrayList<XmlContact> xmlContacts;
    public static XmlContact userContact;

    // Instagram
    public static String instagramToken;
    public static InstagramApi instagramApi;

    // SharedPreferences
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    // new photos
    private NewPhotosService newPhotosService;
    public static ScheduledFuture newPhotosSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                getResources().getColor(android.R.color.transparent)));

        // Configure Cast device discovery
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(getResources()
                        .getString(R.string.app_id))).build();
        mMediaRouterCallback = new MyMediaRouterCallback();

        writeTempFile();

        // set contacts from xml file
        xmlContacts = XmlParser.parseContactsXml();
        userContact = XmlParser.parseOwnerXml();

        // instagram
        instagramApi = new InstagramApi(this);

        // get instagram token
        settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        getInstagramInfo();

        //New Photos
        newPhotosService = new NewPhotosService(this);
        startCheckNewDataService();

        // default fragment
        MenuFragment menuFragment = new MenuFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, menuFragment).addToBackStack("");
        transaction.commit();
    }

    private void startCheckNewDataService() {
        ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
        scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
        newPhotosSchedule = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                newPhotosService.getRunnable(), 0, 1, TimeUnit.MINUTES);
    }

    public void writeTempFile() {
        File myFile = new File(Environment.getExternalStorageDirectory(), "SocialConnContacts.xml");
        try {
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("<?xml version=\"1.0\"?> <owners> <owner> <id>0</id> <nickname>Jose</nickname> <photo></photo> <email>jose.wt@gmail.com</email> <skype>ochoadelorenzi</skype> <instagram></instagram> </owner> </owners> <contacts> <contact> <id>0</id> <nickname>Jose</nickname> <photo> http://comicsalliance.com/files/2012/07/asmsdccpanelmain-1342466154.jpg </photo> <email>jose.wt@gmail.com</email> <skype>ochoadelorenzi</skype> <instagram></instagram> </contact> <contact> <id>1</id> <nickname>Natalia</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>nacha.sanmartin@gmail.com</email> <skype>nat_saintmartins</skype> <instagram>nat_saintmartins</instagram> </contact> <contact> <id>2</id> <nickname>Pia</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>....asd@gmail.com</email> <skype>carla.sambrizzi</skype> <instagram>piacamilaa_</instagram> </contact> <contact> <id>3</id> <nickname>Juanma</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>juan.ochoa@ug.uchile.cl</email> <skype>juanma8a</skype> <instagram></instagram> </contact> <contact> <id>5</id> <nickname>Daniel</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>daniel-ochoa@hotmail.com</email> <skype>danielochoa66</skype> <instagram></instagram> </contact> <contact> <id>6</id> <nickname>Pablo</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>pablo_8a@live.com</email> <skype>pabloj.8a</skype> <instagram></instagram> </contact> </contacts>");
//            myOutWriter.append("<?xml version=\"1.0\"?> <owners> <owner> <id>0</id> <nickname>Jose</nickname> <photo></photo> <email>jose.wt@gmail.com</email> <skype>ochoadelorenzi</skype> <instagram></instagram> </owner> </owners> <contacts> <contact> <id>1</id> <nickname>Natalia</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>nacha.sanmartin@gmail.com</email> <skype>nat_saintmartins</skype> <instagram>nat_saintmartins</instagram> </contact> </contacts>");
//            myOutWriter.append("<?xml version=\"1.0\"?> <owners> <owner> <id>0</id> <nickname>Jose</nickname> <photo></photo> <email>jose.wt@gmail.com</email> <skype>ochoadelorenzi</skype> <instagram></instagram> </owner> </owners> <contacts><contact> <id>0</id> <nickname>Jose</nickname> <photo> http://comicsalliance.com/files/2012/07/asmsdccpanelmain-1342466154.jpg </photo> <email>jose.wt@gmail.com</email> <skype>ochoadelorenzi</skype> <instagram></instagram> </contact>  <contact> <id>1</id> <nickname>Natalia</nickname> <photo> http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png </photo> <email>nacha.sanmartin@gmail.com</email> <skype>nat_saintmartins</skype> <instagram>nat_saintmartins</instagram> </contact> </contacts>");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getInstagramInfo() {
        String auth_token_string = settings.getString(
                getApplicationContext().getString(R.string.instagram_token),
                "");
        instagramToken = auth_token_string;
        instagramApi.searchSelf();
        instagramApi.getFollowingInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start media router discovery
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        // End media router discovery
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        teardown(true);
        newPhotosSchedule.cancel(true);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider
                = (MediaRouteActionProvider) MenuItemCompat
                .getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }

    /**
     * Callback for MediaRouter events
     */
    private class MyMediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo info) {
            Log.d(TAG, "onRouteSelected");
            // Handle the user route selection.
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: info=" + info);
            teardown(false);
            mSelectedDevice = null;
        }
    }

    /**
     * Start the receiver app
     */
    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                    teardown(true);
                }

            };
            // Connect to Google Play services
            mConnectionCallbacks = new ConnectionCallbacks();
            mConnectionFailedListener = new ConnectionFailedListener();
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending
                // execution.
                return;
            }

            try {
                if (mWaitingForReconnect) {
                    mWaitingForReconnect = false;

                    // Check if the receiver app is still running
                    if ((connectionHint != null)
                            && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                        Log.d(TAG, "App  is no longer running");
                        teardown(true);
                    } else {
                        // Re-create the custom message channel
                        try {
                            Cast.CastApi.setMessageReceivedCallbacks(
                                    mApiClient,
                                    mHelloWorldChannel.getNamespace(),
                                    mHelloWorldChannel);
                        } catch (IOException e) {
                            Log.e(TAG, "Exception while creating channel", e);
                        }
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi.launchApplication(mApiClient, getString(R.string.app_id), false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            Log.d(TAG,
                                                    "ApplicationConnectionResultCallback.onResult:"
                                                            + status.getStatusCode());
                                            if (status.isSuccess()) {
                                                ApplicationMetadata applicationMetadata = result
                                                        .getApplicationMetadata();
                                                mSessionId = result.getSessionId();
                                                String applicationStatus = result
                                                        .getApplicationStatus();
                                                boolean wasLaunched = result.getWasLaunched();
                                                Log.d(TAG, "application name: "
                                                        + applicationMetadata.getName()
                                                        + ", status: " + applicationStatus
                                                        + ", sessionId: " + mSessionId
                                                        + ", wasLaunched: " + wasLaunched);
                                                mApplicationStarted = true;

                                                // Create the custom message
                                                // channel
                                                mHelloWorldChannel = new HelloWorldChannel();
                                                try {
                                                    Cast.CastApi.setMessageReceivedCallbacks(
                                                            mApiClient,
                                                            mHelloWorldChannel.getNamespace(),
                                                            mHelloWorldChannel);

                                                    ContactsSendMessageCallBack contactsSendMessageCallBack = new ContactsSendMessageCallBack(MainActivity.this);

                                                    Cast.CastApi.setMessageReceivedCallbacks(
                                                            mApiClient,
                                                            contactsSendMessageCallBack.getNamespace()
                                                            , contactsSendMessageCallBack);

                                                    Cast.CastApi.setMessageReceivedCallbacks(
                                                            mApiClient,
                                                            getString(R.string.photo_seen_namespace)
                                                            , newPhotosService);

                                                } catch (IOException e) {
                                                    Log.e(TAG, "Exception while creating channel",
                                                            e);
                                                }

                                            } else {
                                                Log.e(TAG, "application could not launch");
                                                teardown(true);
                                            }
                                        }
                                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch application", e);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended");
            mWaitingForReconnect = true;
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");

            teardown(false);
        }
    }

    /**
     * Tear down the connection to the receiver
     */
    private void teardown(boolean selectDefaultRoute) {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(mApiClient, mSessionId);
                        if (mHelloWorldChannel != null) {
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    mHelloWorldChannel.getNamespace());
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    getString(R.string.send_message_select_contact_namespace));
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    getString(R.string.photo_seen_namespace));
                            mHelloWorldChannel = null;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        if (selectDefaultRoute) {
            mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;
    }

    class HelloWorldChannel implements MessageReceivedCallback {

        /**
         * @return custom namespace
         */
        public String getNamespace() {
            return getString(R.string.namespace);
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace,
                                      String message) {
            Log.d(TAG, "onMessageReceived: " + namespace + message);
        }

    }

    public GoogleApiClient getmApiClient() {
        return mApiClient;
    }

    public NewPhotosService getNewPhotosService() {
        return newPhotosService;
    }
}
