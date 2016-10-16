/*
 * This file is part of C64 Tribute Watch Face
 * Copyright (C) 2014  Thomas Kuenneth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.c64watchface;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * This class represents the settings activity for the watch face on the wearable device.
 *
 * @author Thomas Kuenneth
 */
public class C64WatchFaceWearableConfigActivity extends Activity
        implements ConnectionCallbacks {

    private static final String TAG = "WearableCfgActivity";

    private GoogleApiClient client;
    private CheckBox cbSecondsVisible;
    private CheckBox cbDateVisible;
    private C64WatchFaceUtil.FetchConfigDataMapCallback fetchConfigDataMapCallback =
            new C64WatchFaceUtil.FetchConfigDataMapCallback() {
                @Override
                public void onConfigDataMapFetched(
                        DataMap config) {
                    if (config.containsKey(
                            C64WatchFaceUtil.KEY_SECONDS_VISIBLE)) {
                        cbSecondsVisible.setChecked(
                                config.getBoolean(
                                        C64WatchFaceUtil.KEY_SECONDS_VISIBLE));
                    }
                    if (config.containsKey(C64WatchFaceUtil.KEY_DATE_VISIBLE)) {
                        cbDateVisible.setChecked(
                                config.getBoolean(
                                        C64WatchFaceUtil.KEY_DATE_VISIBLE));
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_config);
        cbSecondsVisible = (CheckBox) findViewById(R.id.checkbox_econds);
        cbDateVisible = (CheckBox) findViewById(R.id.checkbox_date);
        CompoundButton.OnCheckedChangeListener l =
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        updateConfigDataItem(cbSecondsVisible.isChecked(),
                                cbDateVisible.isChecked());
                    }
                };
        cbSecondsVisible.setOnCheckedChangeListener(l);
        cbDateVisible.setOnCheckedChangeListener(l);
        client = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "onConnectionFailed: " + result);
                        }
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnected: " + bundle);
        }
        C64WatchFaceUtil.fetchConfigDataMap(client,
                fetchConfigDataMapCallback
        );
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    }

    private void updateConfigDataItem(final boolean seconds,
                                      final boolean dateVisible) {
        DataMap map = new DataMap();
        map.putBoolean(C64WatchFaceUtil.KEY_SECONDS_VISIBLE,
                seconds);
        map.putBoolean(C64WatchFaceUtil.KEY_DATE_VISIBLE,
                dateVisible);
        C64WatchFaceUtil.overwriteKeysInConfigDataMap(client, map);
    }
}
