package com.thomaskuenneth.connectivitymanagerdemo;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

public class ConnectivityManagerDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.textview);
        ConnectivityManager mgr =
                getSystemService(ConnectivityManager.class);
        for (Network network : mgr.getAllNetworks()) {
            NetworkInfo n = mgr.getNetworkInfo(network);
            tv.append(n.getTypeName() + " ("
                    + n.getSubtypeName() + ")\n");
            tv.append("isAvailable(): " + n.isAvailable() + "\n");
            tv.append("isConnected(): " + n.isConnected() + "\n");
            tv.append("roaming ist " + (n.isRoaming() ? "ein" : "aus")
                    + "\n\n");
        }
    }
}
