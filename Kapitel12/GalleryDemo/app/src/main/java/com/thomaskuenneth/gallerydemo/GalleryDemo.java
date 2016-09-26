package com.thomaskuenneth.gallerydemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class GalleryDemo extends Activity {

    private static final int RQ_GALLERY_PICK = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RQ_GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    Intent i2 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i2);
                }
            } else {
                finish();
            }
        }
    }
}