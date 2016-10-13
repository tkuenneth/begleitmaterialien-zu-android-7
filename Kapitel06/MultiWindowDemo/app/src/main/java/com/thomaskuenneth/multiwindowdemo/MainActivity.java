package com.thomaskuenneth.multiwindowdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG =
            MainActivity.class.getSimpleName();

    private final StringBuilder sb = new StringBuilder();

    private TextView tv;
    private AnimatedNumberView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
        view = (AnimatedNumberView) findViewById(R.id.anim);
        Button bt = (Button) findViewById(R.id.launch);
        bt.setOnClickListener(e -> {
            Intent i = new Intent(this, ChildActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        view.setEnabled(false);
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateTextView();
        view.setEnabled(true);
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onPictureInPictureModeChanged(
            boolean isInPictureInPictureMode) {
        Log.d(TAG, "onPictureInPictureModeChanged(): " +
                isInPictureInPictureMode);
    }

    @Override
    public void onMultiWindowModeChanged(
            boolean isInMultiWindowMode) {
        Log.d(TAG, "onMultiWindowModeChanged(): " +
                isInMultiWindowMode);
    }

    private void updateTextView() {
        sb.setLength(0);
        sb.append("isInMultiWindowMode(): ")
                .append(isInMultiWindowMode())
                .append("\n");
        sb.append("isInPictureInPictureMode(): ")
                .append(isInPictureInPictureMode())
                .append("\n");
        tv.setText(sb.toString());
    }
}
