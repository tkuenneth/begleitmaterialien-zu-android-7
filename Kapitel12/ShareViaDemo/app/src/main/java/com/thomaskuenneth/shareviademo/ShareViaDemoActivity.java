package com.thomaskuenneth.shareviademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ShareViaDemoActivity extends Activity {

    private static final int RQ_PICK = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, RQ_PICK);
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK) &&
                (requestCode == RQ_PICK) &&
                (data != null)) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    data.getData());
            startActivity(intent);
        }
    }
}