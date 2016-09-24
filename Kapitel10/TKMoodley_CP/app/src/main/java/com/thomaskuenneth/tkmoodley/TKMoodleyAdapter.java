package com.thomaskuenneth.tkmoodley;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class TKMoodleyAdapter extends CursorAdapter {

    private final Date date;

    private static final DateFormat DF_DATE = SimpleDateFormat
            .getDateInstance(DateFormat.MEDIUM);
    private static final DateFormat DF_TIME = SimpleDateFormat
            .getTimeInstance(DateFormat.MEDIUM);

    private LayoutInflater inflator;

    TKMoodleyAdapter(Context context) {
        super(context, null, 0);
        date = new Date();
        inflator = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int ciMood =
                cursor.getColumnIndex(TKMoodleyOpenHandler.MOOD_MOOD);
        int ciTimeMillis =
                cursor.getColumnIndex(TKMoodleyOpenHandler.MOOD_TIME);
        ImageView image = (ImageView) view.findViewById(R.id.icon);
        int mood = cursor.getInt(ciMood);
        if (mood == TKMoodleyOpenHandler.MOOD_FINE) {
            image.setImageResource(R.drawable.smiley_gut);
        } else if (mood == TKMoodleyOpenHandler.MOOD_OK) {
            image.setImageResource(R.drawable.smiley_ok);
        } else {
            image.setImageResource(R.drawable.smiley_schlecht);
        }
        TextView textview1 = (TextView) view.findViewById(R.id.text1);
        TextView textview2 = (TextView) view.findViewById(R.id.text2);
        long timeMillis = cursor.getLong(ciTimeMillis);
        date.setTime(timeMillis);
        textview1.setText(DF_DATE.format(date));
        textview2.setText(DF_TIME.format(date));
    }

    @Override
    public View newView(Context context, Cursor cursor,
                        ViewGroup parent) {
        return inflator.inflate(R.layout.icon_text_text, null);
    }
}
