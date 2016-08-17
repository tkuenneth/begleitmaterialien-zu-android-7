package com.thomaskuenneth.jobschedulerdemo1;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;

import java.util.List;


public class JobSchedulerDemo1Activity extends Activity {

    private static final int JOB_ID = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        // ausstehende Jobs anzeigen
        List<JobInfo> jobs = jobScheduler.getAllPendingJobs();
        StringBuilder sb = new StringBuilder();
        for (JobInfo info : jobs) {
            sb.append(info.toString() + "\n");
        }
        if (sb.length() == 0) {
            sb.append(getString(R.string.no_jobs));
        }

        // die Klasse des Jobs
        ComponentName serviceEndpoint = new ComponentName(this, JobSchedulerDemo1Service.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceEndpoint)
                // alle 10 Sekunden wiederholen
                .setPeriodic(10000)
                // nur wenn das Ladekabel angeschlossen ist
                .setRequiresCharging(true)
                .build();

        // die Ausführung planen
        jobScheduler.schedule(jobInfo);
    }
}
