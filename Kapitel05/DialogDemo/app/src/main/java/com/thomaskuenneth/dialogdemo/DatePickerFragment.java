package com.thomaskuenneth.dialogdemo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static final String TAG = DatePickerFragment.class.getSimpleName();

    private DatePickerDialog.OnDateSetListener l;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialog.OnDateSetListener) {
            l = (DatePickerDialog.OnDateSetListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // das aktuelle Datum als Voreinstellung nehmen
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // einen DatePickerDialog erzeugen und zurückliefern
        return new DatePickerDialog(getActivity(), l, year, month, day);
    }
}
