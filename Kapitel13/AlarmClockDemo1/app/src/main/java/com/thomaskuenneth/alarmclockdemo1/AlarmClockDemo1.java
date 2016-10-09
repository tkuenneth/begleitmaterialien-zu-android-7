package com.thomaskuenneth.alarmclockdemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.widget.Button;
import android.widget.RadioGroup;

public class AlarmClockDemo1 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        RadioGroup group = (RadioGroup) findViewById(R.id.group);
        group.check(R.id.alarm);
        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener((v) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.alarm:
                    fireAlarm();
                    break;
                case R.id.timer:
                    fireTimer();
                    break;
            }
        });
    }

    private void fireAlarm() {
        Intent alarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarm.putExtra(AlarmClock.EXTRA_MESSAGE, "Ein Alarm");
        alarm.putExtra(AlarmClock.EXTRA_HOUR, 20);
        alarm.putExtra(AlarmClock.EXTRA_MINUTES, 0);
        alarm.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        startActivity(alarm);
    }

    private void fireTimer() {
        Intent timer = new Intent(AlarmClock.ACTION_SET_TIMER);
        timer.putExtra(AlarmClock.EXTRA_MESSAGE, "Ein Timer");
        timer.putExtra(AlarmClock.EXTRA_LENGTH, 90);
        timer.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        startActivity(timer);
    }
}