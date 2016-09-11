package com.thomaskuenneth.webviewdemo3;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {

    private Context mContext;

    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public String getHeadline() {
        return mContext.getString(R.string.headline);
    }

    @JavascriptInterface
    public void message(String m) {
        Toast toast = Toast.makeText(mContext, m, Toast.LENGTH_LONG);
        toast.show();
    }
}
