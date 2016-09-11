package com.thomaskuenneth.webviewdemo1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebView;
import android.widget.Button;


public class WebViewDemo1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("https://www.rheinwerk-verlag.de/");
        final Button b1 = (Button) findViewById(R.id.intent);
        b1.setOnClickListener((e) -> {
            // Webseite mit einem Intent anzeigen
            Uri uri = Uri.parse("https://www.rheinwerk-verlag.de/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        final Button b2 = (Button) findViewById(R.id.html);
        b2.setOnClickListener((e) -> {
            // URL-Kodierung
            String html1 = "<html><body><p>Hallo Android</p></body></html>";
            webview.loadData(html1, "text/html", null);
        });
        final Button b3 = (Button) findViewById(R.id.base64);
        b3.setOnClickListener((e) -> {
            // Base64-Kodierung
            String html2 =
                    "<html><body><p>Hallo Welt</p></body></html>";
            String base64 = Base64.encodeToString(html2.getBytes(), Base64.DEFAULT);
            webview.loadData(base64, "text/html", "base64");
        });
    }
}
