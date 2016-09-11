package com.thomaskuenneth.webservicedemo1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebserviceDemo1Activity extends Activity {

    private static final String TAG =
            WebserviceDemo1Activity.class.getSimpleName();
    private static final String API_KEY = "...";
    private static final String URL
            = "https://www.googleapis.com/urlshortener/v1/url";
    private static final Pattern PATTERN_CHARSET
            = Pattern.compile(".*charset\\s*=\\s*(.*)$");
    private static final String ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText input = (EditText) findViewById(R.id.input);
        final TextView output = (TextView) findViewById(R.id.output);
        final Button shorten = (Button) findViewById(R.id.shorten);
        shorten.setOnClickListener((v) -> {
            String url = input.getText().toString();
            String json = "{\"longUrl\": \"" + url + "\"}";
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    return shortenURL(params[0]);
                }

                @Override
                protected void onPostExecute(String result) {
                    output.setText(result);
                }
            }.execute(json);
        });
        final Button show = (Button) findViewById(R.id.show);
        show.setOnClickListener((v) -> {
            String json = output.getText().toString();
            try {
                JSONObject o = new JSONObject(json);
                if (o.has(ID)) {
                    String url = o.getString(ID);
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                Log.e(TAG, "onClick()", e);
            }
        });
    }

    private String shortenURL(String json) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(URL + "?key=" + API_KEY);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // Verbindung konfigurieren
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            byte[] data = json.getBytes();
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/json; charset="
                            + Charset.defaultCharset().name());
            // war früher für den Aufruf des Service erforderlich
            // httpURLConnection.setRequestProperty("apikey", API_KEY);
            httpURLConnection.setFixedLengthStreamingMode(data.length);
            // Daten senden
            httpURLConnection.getOutputStream().write(data);
            httpURLConnection.getOutputStream().flush();
            String contentType = httpURLConnection.getContentType();
            String charSet = "ISO-8859-1";
            if (contentType != null) {
                Matcher m = PATTERN_CHARSET.matcher(contentType);
                if (m.matches()) {
                    charSet = m.group(1);
                }
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        httpURLConnection.getInputStream(), charSet);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                int i;
                while ((i = bufferedReader.read()) != -1) {
                    sb.append((char) i);
                }
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "shortenURL()", e);
                }
            } else {
                Log.d(TAG, "responseCode: " + responseCode);
            }
        } catch (Throwable tr) {
            Log.e(TAG, "Fehler beim Zugriff auf " + URL, tr);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return sb.toString();
    }
}
