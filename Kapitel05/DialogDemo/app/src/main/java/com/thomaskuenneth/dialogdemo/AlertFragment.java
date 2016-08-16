package com.thomaskuenneth.dialogdemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertFragment extends DialogFragment {

    public static final String TAG =
            AlertFragment.class.getSimpleName();

    private DialogInterface.OnClickListener l;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogInterface.OnClickListener) {
            l = (DialogInterface.OnClickListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Builder instanziieren
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        // Builder konfigurieren
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.close, l);
        // AlertDialog erzeugen und zurückliefern
        return builder.create();
    }
}
