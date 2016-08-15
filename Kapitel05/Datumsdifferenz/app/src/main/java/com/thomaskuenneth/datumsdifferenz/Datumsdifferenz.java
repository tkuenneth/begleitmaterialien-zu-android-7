package com.thomaskuenneth.datumsdifferenz;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class Datumsdifferenz extends Activity {

	private Calendar cal1, cal2;

	private TextView tv;
	private DatePicker dp1, dp2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		cal1 = Calendar.getInstance();
		cal2 = Calendar.getInstance();

		dp1 = (DatePicker) findViewById(R.id.date1);
		dp2 = (DatePicker) findViewById(R.id.date2);
		tv = (TextView) findViewById(R.id.textview_result);

		final Button b = (Button) findViewById(R.id.button_calc);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				berechnen();
			}
		});

		berechnen();
	}

	private void berechnen() {
		updateCalendarFromDatePicker(cal1, dp1);
		updateCalendarFromDatePicker(cal2, dp2);
		if (cal2.before(cal1)) {
			Calendar temp = cal1;
			cal1 = cal2;
			cal2 = temp;
		}
		int days = 0;
		while ((cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR))
				|| (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH))
				|| (cal1.get(Calendar.DAY_OF_MONTH) != cal2
						.get(Calendar.DAY_OF_MONTH))) {
			days += 1;
			cal1.add(Calendar.DAY_OF_YEAR, 1);
		}
		tv.setText(getString(R.string.template, days));
	}

	private void updateCalendarFromDatePicker(Calendar cal, DatePicker dp) {
		cal.set(Calendar.YEAR, dp.getYear());
		cal.set(Calendar.MONTH, dp.getMonth());
		cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
	}
}