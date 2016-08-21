package com.thomaskuenneth.anrufdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AnrufDemoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Button buttonDialog = (Button) findViewById(R.id.dialog);
        buttonDialog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Wähldialog anzeigen
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + "+49 (999) 44 55 66"));
                startActivity(intent);
            }
        });
        final Button buttonSofort = (Button) findViewById(R.id.sofort);
        buttonSofort.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // sofort wählen
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + "+49 (999) 44 55 66"));
                startActivity(intent);
            }
        });

        // SMS senden
//        String telnr = "123-456-789";
//        Uri smsUri = Uri.parse("smsto:" + telnr);
//        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
//        sendIntent.putExtra("sms_body", "Hier steht der Text der Nachricht...");
//        startActivity(sendIntent);
    }
}