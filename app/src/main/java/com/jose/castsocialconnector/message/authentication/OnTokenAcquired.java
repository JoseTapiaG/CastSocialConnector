package com.jose.castsocialconnector.message.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

import com.jose.castsocialconnector.main.MainActivity;

/**
 * Created by Jose Manuel on 22/04/2016.
 */
public class OnTokenAcquired implements AccountManagerCallback<Bundle> {
    private Account account;
    private MainActivity activity;


    public OnTokenAcquired(MainActivity activity, Account account) {
        this.account = account;
        this.activity = activity;
    }

    @Override
    public void run(AccountManagerFuture<Bundle> result) {
        try {
            Bundle bundle = result.getResult();
            activity.setOauthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
            activity.getUserContact().setEmail(account.name);
//            new CreateImapStore(MainActivity.this, emailAccount, oauthToken).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
