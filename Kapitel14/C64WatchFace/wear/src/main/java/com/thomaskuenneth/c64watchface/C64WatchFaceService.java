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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.TimeZone;

/**
 * This class implements a Commodore 64 like watch face.
 *
 * @author Thomas Kuenneth
 */
public class C64WatchFaceService extends CanvasWatchFaceService {

    private static final String TAG =
            C64WatchFaceService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine
            implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private static final int INTERACTIVE_UPDATE_RATE_MS = 333;
        private static final int MSG_UPDATE_TIME = 0;

        // Commodore 64 colour 14 (http://unusedino.de/ec64/technical/misc/vic656x/colors/)
        private static final int LIGHT_BLUE = 0xff6C5EB5;

        // Commodore 64 colour 6
        private static final int BLUE = 0xff352879;

        // black
        private static final int BLACK = 0xff000000;

        // white
        private static final int WHITE = 0xffffffff;

        // calculazed text height
        private float last;

        // C64 cursor visible
        private boolean c64CursorVisible;

        // seconds visible?
        private boolean seconds;

        // date visible?
        private boolean dateVisible;

        /* handler to update the time 3 times a second in interactive mode */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        c64CursorVisible = !c64CursorVisible;
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs =
                                    INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs
                                            % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(
                                    MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        GoogleApiClient mGoogleApiClient = new
                GoogleApiClient.Builder(C64WatchFaceService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        /* receiver to update the time zone */
        final BroadcastReceiver mTimeZoneReceiver =
                new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver;

        Paint borderPaint;
        Paint backgroundPaint;
        Paint textPaint;
        Time mTime;
        boolean isRound = false;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            seconds = false;
            dateVisible = false;
            mTime = new Time();
            mRegisteredTimeZoneReceiver = false;
            last = -1;
            textPaint = new Paint();
            Typeface typface = Typeface.createFromAsset(getAssets(),
                    "C64_Pro_Mono-STYLE.ttf");
            textPaint.setTypeface(typface);
            borderPaint = new Paint();
            backgroundPaint = new Paint();
            setupPaint(false);
                /* configure the system UI */
            setWatchFaceStyle(
                    new WatchFaceStyle.Builder(C64WatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle
                            .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setStatusBarGravity(Gravity.START | Gravity.TOP)
                    .setHotwordIndicatorGravity(
                            Gravity.CENTER_HORIZONTAL |
                            Gravity.BOTTOM)
                    .build());
            c64CursorVisible = false;
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            isRound = insets.isRound();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            setupPaint(inAmbientMode);
            onVisibilityChanged(isVisible());
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();
            String strDate;
            if (dateVisible) {
                String strWeekday = mTime.format("%a");
                if (strWeekday.endsWith(".")) {
                    strWeekday = strWeekday.substring(0,
                            strWeekday.length() - 1);
                }
                strDate = strWeekday + " " + mTime.format("%d");
            } else {
                strDate = "";
            }
            String patternTime;
            if (DateFormat.is24HourFormat(getBaseContext())) {
                if (seconds) {
                    patternTime = mTime.format("%H:%M:%S");
                } else {
                    patternTime = mTime.format("%H:%M");
                }
            } else {
                if (seconds) {
                    patternTime = mTime.format("%I:%M:%S %p");
                } else {
                    patternTime = mTime.format("%I:%M %p");
                }
            }
            String strTime = mTime.format(patternTime);
            while (strDate.length() > strTime.length()) {
                strTime = strTime + " ";
            }
            int w = bounds.width();
            int h = bounds.height();
            int borderHeight = (int) (((float) h / 100f) * 5f);
            int borderWidth = (int) (((float) w / 100f) * 5f);
            canvas.drawPaint(borderPaint);
            Rect r = new Rect(borderWidth,
                    borderHeight, w - 1 - borderWidth,
                    h - borderHeight - 1);

            if (isRound) {
                canvas.drawCircle(bounds.width() / 2,
                        bounds.height() / 2,
                        (bounds.width() - borderWidth) / 2,
                        backgroundPaint);
            } else {
                canvas.drawRect(r, backgroundPaint);
            }

            if (last == -1) {
                int maxWidth = r.width();
                float size = 12f;
                last = size;
                while (true) {
                    textPaint.setTextSize(size);
                    float current = textPaint.measureText(strTime);
                    if (current < maxWidth) {
                        last = size;
                        size += 4;
                    } else {
                        break;
                    }
                }
                textPaint.setTextSize(last);
            }
            int x = (w - (int) textPaint.measureText(strTime)) / 2;
            int th = dateVisible ? 2 * (int) last : (int) last;
            int y = ((h - th) / 2) - (int) textPaint.ascent();
            canvas.drawText(strTime, x, y, textPaint);
            if (dateVisible) {
                y += last;
                canvas.drawText(strDate, x, y, textPaint);
            }
            if (!isInAmbientMode()) {
                y += last;
                String a = c64CursorVisible ? "\u2588" : " ";
                canvas.drawText(a, x, y, textPaint);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mGoogleApiClient.connect();
                registerReceiver();
                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
                if (mGoogleApiClient != null &&
                        mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient,
                            this);
                    mGoogleApiClient.disconnect();
                }
            }
            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode), so we may need to start or stop the timer
            updateTimer();
        }

        @Override // DataApi.DataListener
        public void onDataChanged(DataEventBuffer dataEvents) {
            try {
                for (DataEvent dataEvent : dataEvents) {
                    if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                        continue;
                    }

                    DataItem dataItem = dataEvent.getDataItem();
                    if (!dataItem.getUri().getPath().equals(
                            C64WatchFaceUtil.PATH_WITH_FEATURE)) {
                        continue;
                    }

                    DataMapItem dataMapItem =
                            DataMapItem.fromDataItem(dataItem);
                    DataMap config = dataMapItem.getDataMap();
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Config DataItem updated:" + config);
                    }
                    updateUiForConfigDataMap(config);
                }
            } finally {
                dataEvents.close();
            }
        }

        @Override  // GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnected: " + connectionHint);
            }
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
            updateConfigDataItemAndUiOnStartup();
        }

        @Override  // GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        }

        @Override  // GoogleApiClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        }

        private void updateUiForConfigDataMap(final DataMap config) {
            boolean uiUpdated = false;
            for (String configKey : config.keySet()) {
                if (configKey.equals(C64WatchFaceUtil.KEY_SECONDS_VISIBLE)) {
                    seconds = config.getBoolean(configKey);
                    uiUpdated = true;
                    continue;
                }
                if (configKey.equals(C64WatchFaceUtil.KEY_DATE_VISIBLE)) {
                    dateVisible = config.getBoolean(configKey);
                    uiUpdated = true;
                    continue;
                }
            }
            if (uiUpdated) {
                last = -1;
                invalidate();
            }
        }

        private void updateConfigDataItemAndUiOnStartup() {
            C64WatchFaceUtil.fetchConfigDataMap(mGoogleApiClient,
                    new C64WatchFaceUtil.FetchConfigDataMapCallback() {
                        @Override
                        public void onConfigDataMapFetched(
                                DataMap startupConfig) {
                            // If the DataItem hasn't been created yet or some keys are missing,
                            // use the default values.
                            addBooleanKeyIfMissing(startupConfig,
                                    C64WatchFaceUtil.KEY_SECONDS_VISIBLE,
                                    false);
                            addBooleanKeyIfMissing(startupConfig,
                                    C64WatchFaceUtil.KEY_DATE_VISIBLE,
                                    false);
                            C64WatchFaceUtil.putConfigDataItem(mGoogleApiClient,
                                    startupConfig);
                            updateUiForConfigDataMap(startupConfig);
                        }
                    }
            );
        }

        private void addBooleanKeyIfMissing(DataMap config,
                                            String key, boolean value) {
            if (!config.containsKey(key)) {
                config.putBoolean(key, value);
            }
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter =
                    new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            C64WatchFaceService.this.registerReceiver(mTimeZoneReceiver,
                    filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            C64WatchFaceService.this.unregisterReceiver(
                    mTimeZoneReceiver);
        }

        private void setupPaint(boolean inAmbientMode) {
            textPaint.setColor(inAmbientMode ? WHITE : LIGHT_BLUE);
            borderPaint.setColor(inAmbientMode ? BLACK : LIGHT_BLUE);
            backgroundPaint.setColor(inAmbientMode ? BLACK : BLUE);
            textPaint.setAntiAlias(!inAmbientMode);
        }
    }
}
