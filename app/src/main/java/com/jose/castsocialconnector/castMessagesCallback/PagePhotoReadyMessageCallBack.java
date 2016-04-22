package com.jose.castsocialconnector.castMessagesCallback;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.google.android.gms.cast.CastDevice;
import com.jose.castsocialconnector.R;
import com.jose.castsocialconnector.photo.AlbumPhotosFragment;
import com.jose.castsocialconnector.photo.NewPhotosFragment;

public class PagePhotoReadyMessageCallBack extends BaseCastMessageCallback {

    public PagePhotoReadyMessageCallBack(Activity activity) {
        super(activity);
    }

    @Override
    public String getNamespace() {
        return activity.getString(R.string.album_photos_ready);
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String nameSpace, String message) {
        if (nameSpace.equals("urn:x-cast:com.jose.cast.sample.album_photo_ready")) {

            switch (message) {
                case "new":
                    changeFragment(new NewPhotosFragment());
                    break;
                case "album":
                    changeFragment(new AlbumPhotosFragment());
                    break;
            }
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
