package com.thomaskuenneth.webviewdemo3;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class WebViewDemo3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final WebView wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/test1.html");
        // wv.loadUrl("file:///android_asset/test2.html");
        // wv.addJavascriptInterface(new WebAppInterface(this), "Android");
    }
}
