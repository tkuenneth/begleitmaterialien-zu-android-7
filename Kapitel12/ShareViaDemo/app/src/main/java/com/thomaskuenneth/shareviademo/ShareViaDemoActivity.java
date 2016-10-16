package com.thomaskuenneth.shareviademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ShareViaDemoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("image/*");
            startActivity(intent);
            finish();
        });
    }
}