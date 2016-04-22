package com.jose.castsocialconnector.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.jose.castsocialconnector.R;

/**
 * Created by Jose Manuel on 22/04/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected void onBackPressed() {
    }

    protected void changeFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
