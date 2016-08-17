package com.thomaskuenneth.uithreaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UIThreadDemo extends Activity {

    private static final String TAG = UIThreadDemo.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView textview = (TextView) findViewById(R.id.textview);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textview.setText(UIThreadDemo.this.getString(R.string.begin));
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "sleep()", e);
                }
                // for (int i = 0; i < 1;);
                textview.setText(UIThreadDemo.this.getString(R.string.end));
            }

//            @Override
//            public void onClick(View v) {
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                        }
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                textview.setText(UIThreadDemo.this.getString(R.string.end));
//                            }
//                        });
//                    }
//                });
//                textview.setText(UIThreadDemo.this.getString(R.string.begin));
//                t.start();
//            }

//            @Override
//            public void onClick(View v) {
//                final Handler h = new Handler();
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            h.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    textview.setText(UIThreadDemo.this
//                                            .getString(R.string.begin));
//                                }
//                            });
//                            Thread.sleep(10000);
//                            h.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    textview.setText(UIThreadDemo.this
//                                            .getString(R.string.end));
//                                }
//                            });
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                });
//                t.start();
//            }

        });
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                textview.setText(Boolean.toString(isChecked));
            }
        });
        checkbox.setChecked(true);
    }
}