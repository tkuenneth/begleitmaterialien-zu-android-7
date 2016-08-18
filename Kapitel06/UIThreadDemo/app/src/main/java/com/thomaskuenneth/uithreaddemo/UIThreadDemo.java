package com.thomaskuenneth.uithreaddemo;

import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UIThreadDemo extends Activity {

    public static final String TAG = UIThreadDemo.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final TextView tv = (TextView) findViewById(R.id.textview);
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                tv.setText(Boolean.toString(isChecked));
            }
        });
        checkbox.setChecked(true);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            // ursprüngliche Version
            @Override
            public void onClick(View v) {
                tv.setText(UIThreadDemo.this.getString(R.string.begin));
                if (checkbox.isChecked()) {
                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sleep()", e);
                    }
                } else {
                    while (true) ;
                }
                tv.setText(UIThreadDemo.this.getString(R.string.end));
            }

            // fehlerhafte Version
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv.setText(UIThreadDemo.this.getString(R.string.begin));
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, "sleep()", e);
//                        }
//                        tv.setText(UIThreadDemo.this.getString(R.string.end));
//                    }
//                }).start();
//            }

            // korrekte Version
//            @Override
//            public void onClick(View v) {
//                final Handler h = new Handler();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            h.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv.setText(UIThreadDemo.this
//                                            .getString(R.string.begin));
//                                }
//                            });
//                            Thread.sleep(10000);
//                            h.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tv.setText(UIThreadDemo.this
//                                            .getString(R.string.end));
//                                }
//                            });
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, "sleep()", e);
//                        }
//                    }
//                }).start();
//            }

            // ebenfalls korrekte Version
//            @Override
//            public void onClick(View v) {
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            Log.e(TAG, "sleep()", e);
//                        }
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                tv.setText(UIThreadDemo.this.getString(
//                                        R.string.end));
//                            }
//                        });
//                    }
//                });
//                tv.setText(UIThreadDemo.this.getString(R.string.begin));
//                t.start();
//            }

        });
    }
}
