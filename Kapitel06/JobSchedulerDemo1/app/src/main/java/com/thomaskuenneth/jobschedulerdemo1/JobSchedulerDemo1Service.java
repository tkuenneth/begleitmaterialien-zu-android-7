package com.thomaskuenneth.jobschedulerdemo1;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class JobSchedulerDemo1Service extends JobService {

    private static final String TAG = JobSchedulerDemo1Service.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "onStartJob()");
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "Job in Aktion");
                jobFinished(params, false);
            }
        });
        t.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false;
    }
}
