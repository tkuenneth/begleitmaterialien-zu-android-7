package com.thomaskuenneth.anrufdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AnrufDemoActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;

    private Button buttonSofort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buttonSofort = (Button) findViewById(R.id.sofort);
        buttonSofort.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // sofort wählen
                Intent intent = new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:+49 (999) 44 55 66"));
                try {
                    startActivity(intent);
                } catch (SecurityException e) {
                    Toast.makeText(AnrufDemoActivity.this,
                            R.string.no_permission,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        boolean allowed = true;
        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            allowed = false;
            requestPermissions(
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
        buttonSofort.setEnabled(allowed);
        final Button buttonDialog = (Button) findViewById(R.id.dialog);
        buttonDialog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Wähldialog anzeigen
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:+49 (999) 44 55 66"));
                startActivity(intent);
            }
        });
        final Button sms = (Button) findViewById(R.id.sms);
        sms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // SMS senden
                String telnr = "123-456-789";
                Uri smsUri = Uri.parse("smsto:" + telnr);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO,
                        smsUri);
                sendIntent.putExtra("sms_body",
                        "Hier steht der Text der Nachricht...");
                startActivity(sendIntent);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) &&
                (grantResults.length > 0
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            buttonSofort.setEnabled(true);
        }
    }
}