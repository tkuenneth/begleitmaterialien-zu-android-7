package com.thomaskuenneth.accountdemo1;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

public class AccountDemo1 extends Activity {

    private static final String TAG =
            AccountDemo1.class.getSimpleName();

    private static final int
            PERMISSIONS_REQUEST_GET_ACCOUNTS = 123;

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.GET_ACCOUNTS},
                    PERMISSIONS_REQUEST_GET_ACCOUNTS);
        } else {
            doIt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_GET_ACCOUNTS) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        }
    }

    private void doIt() {
        AccountManager am = AccountManager.get(this);
        try {
            Account[] accounts = am.getAccounts();
            Log.d(TAG, "Anzahl gefundener Konten: "
                    + accounts.length);
            for (Account account : accounts) {
                Log.d(TAG, account.toString());
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getAccounts()", e);
        }
        finish();
    }
}