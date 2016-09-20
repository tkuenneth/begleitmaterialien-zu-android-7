package com.thomaskuenneth.druckdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class DruckDemo1Activity extends Activity {

    private static final String TAG = DruckDemo1Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // WebView für den Druck instantiieren
        final WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // PrintManager-Instanz ermitteln
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
                // Der Adapter stellt den Dokumentinhalt bereit
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Dokumentname");
                // Druckauftrag erstellen und übergeben
                String jobName = getString(R.string.app_name) + " Dokument";
                PrintJob printJob = printManager.print(jobName, printAdapter,
                        new PrintAttributes.Builder().build());
                Log.d(TAG, printJob.getInfo().toString());
            }
        });

        String htmlDocument = "<html><body><h1>Hallo Android</h1>" +
                "<p><img src=\"ic_launcher.png\" /><br />Ein Test</p></body></html>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlDocument, "text/HTML", "UTF-8", null);
    }
}
