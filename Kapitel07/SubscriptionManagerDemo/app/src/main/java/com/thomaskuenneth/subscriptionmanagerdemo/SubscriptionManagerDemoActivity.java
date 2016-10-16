package com.thomaskuenneth.subscriptionmanagerdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class SubscriptionManagerDemoActivity extends Activity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE =
            123;
    private static final String TAG =
            SubscriptionManagerDemoActivity.class.getSimpleName();

    private SubscriptionManager m;
    private final SubscriptionManager.OnSubscriptionsChangedListener l =
            new SubscriptionManager.OnSubscriptionsChangedListener() {

                @Override
                public void onSubscriptionsChanged() {
                    Log.d(TAG, "onSubscriptionsChanged()");
                    output();
                }
            };
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        layout = (LinearLayout) findViewById(R.id.layout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            output();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            output();
        } else {
            m = null;
            requestPermissions(new String[]
                            {Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (m != null) {
            m.removeOnSubscriptionsChangedListener(l);
        }
    }

    private void output() {
        m = SubscriptionManager.from(this);
        m.addOnSubscriptionsChangedListener(l);
        List<SubscriptionInfo> l = m.getActiveSubscriptionInfoList();
        layout.removeAllViews();
        if (l != null) {
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            for (SubscriptionInfo i : l) {
                Log.d(TAG, "getCarrierName(): "
                        + i.getCarrierName().toString());
                Log.d(TAG, "getDisplayName(): "
                        + i.getDisplayName().toString());
                Log.d(TAG, "getDataRoaming(): "
                        + i.getDataRoaming());
                ImageView imageview = new ImageView(this);
                imageview.setLayoutParams(params);
                imageview.setImageBitmap(i.createIconBitmap(this));
                layout.addView(imageview);
            }
        }
        layout.invalidate();
    }
}
