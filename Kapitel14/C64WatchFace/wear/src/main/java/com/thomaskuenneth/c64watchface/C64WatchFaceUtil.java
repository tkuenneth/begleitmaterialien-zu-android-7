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

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * This class provides utility methods for reading and writing config data.
 *
 * @author Thomas Kuenneth
 */
public class C64WatchFaceUtil {

    private static final String TAG =
            C64WatchFaceUtil.class.getSimpleName();

    /**
     * The path for the {@link DataItem} containing {@link C64WatchFaceService} configuration.
     */
    public static final String PATH_WITH_FEATURE =
            "/watch_face_config/C64";

    /**
     * The {@link DataMap} key for {@link C64WatchFaceService} show seconds property.
     */
    public static final String KEY_SECONDS_VISIBLE = "SECONDS";

    /**
     * The {@link DataMap} key for {@link C64WatchFaceService} show date property.
     */
    public static final String KEY_DATE_VISIBLE = "DATE";

    /**
     * Callback interface to perform an action with the current config {@link DataMap} for
     * {@link C64WatchFaceService}.
     */
    public interface FetchConfigDataMapCallback {
        /**
         * Callback invoked with the current config {@link DataMap} for
         * {@link C64WatchFaceService}.
         */
        void onConfigDataMapFetched(DataMap config);
    }

    private C64WatchFaceUtil() { }

    /**
     * Overwrites (or sets, if not present) the keys in the current config {@link DataItem} with
     * the ones appearing in the given {@link DataMap}. If the config DataItem doesn't exist,
     * it's created.
     * <p>
     * It is allowed that only some of the keys used in the config DataItem appear in
     * {@code configKeysToOverwrite}. The rest of the keys remains unmodified in this case.
     */
    public static void overwriteKeysInConfigDataMap(
            final GoogleApiClient googleApiClient,
            final DataMap configKeysToOverwrite) {

        fetchConfigDataMap(googleApiClient,
                new FetchConfigDataMapCallback() {
                    @Override
                    public void onConfigDataMapFetched(
                            DataMap currentConfig) {
                        DataMap overwrittenConfig = new DataMap();
                        overwrittenConfig.putAll(currentConfig);
                        overwrittenConfig.putAll(configKeysToOverwrite);
                        putConfigDataItem(googleApiClient,
                                overwrittenConfig);
                    }
                }
        );
    }

    /**
     * Asynchronously fetches the current config {@link DataMap} for {@link C64WatchFaceService}
     * and passes it to the given callback.
     * <p>
     * If the current config {@link DataItem} doesn't exist, it isn't created and the callback
     * receives an empty DataMap.
     */
    public static void fetchConfigDataMap(final GoogleApiClient client,
                                          final FetchConfigDataMapCallback cb) {
        Wearable.NodeApi.getLocalNode(client).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(
                            NodeApi.GetLocalNodeResult result) {
                        String localNode =
                                result.getNode().getId();
                        Uri uri = new Uri.Builder()
                                .scheme("wear")
                                .path(PATH_WITH_FEATURE)
                                .authority(localNode)
                                .build();
                        Wearable.DataApi.getDataItem(client, uri)
                                .setResultCallback(
                                        new DataItemResultCallback(cb));
                    }
                }
        );
    }

    /**
     * Overwrites the current config {@link DataItem}'s {@link DataMap} with {@code newConfig}.
     * If the config DataItem doesn't exist, it's created.
     */
    public static void putConfigDataItem(GoogleApiClient client,
                                         DataMap newConfig) {
        PutDataMapRequest putDataMapRequest =
                PutDataMapRequest.create(PATH_WITH_FEATURE);
        DataMap configToPut = putDataMapRequest.getDataMap();
        configToPut.putAll(newConfig);
        Wearable.DataApi.putDataItem(client,
                putDataMapRequest.asPutDataRequest())
                .setResultCallback(
                        new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(
                            DataApi.DataItemResult dataItemResult) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "putDataItem result status: " +
                                    dataItemResult.getStatus());
                        }
                    }
                });
    }

    private static class DataItemResultCallback
            implements ResultCallback<DataApi.DataItemResult> {

        private final FetchConfigDataMapCallback mCallback;

        public DataItemResultCallback(FetchConfigDataMapCallback cb) {
            mCallback = cb;
        }

        @Override
        public void onResult(DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess()) {
                if (dataItemResult.getDataItem() != null) {
                    DataItem configDataItem =
                            dataItemResult.getDataItem();
                    DataMapItem dataMapItem =
                            DataMapItem.fromDataItem(configDataItem);
                    DataMap config = dataMapItem.getDataMap();
                    mCallback.onConfigDataMapFetched(config);
                } else {
                    mCallback.onConfigDataMapFetched(new DataMap());
                }
            }
        }
    }
}
