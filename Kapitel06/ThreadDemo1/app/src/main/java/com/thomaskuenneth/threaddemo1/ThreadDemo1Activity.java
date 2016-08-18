package com.thomaskuenneth.threaddemo1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ThreadDemo1Activity extends Activity {

    private static final String TAG =
            ThreadDemo1Activity.class.getSimpleName();

    private volatile boolean keepRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, Thread.currentThread().getName());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run()-Methode wurde aufgerufen");
            }
        };
        Thread t = new Thread(r);
        t.start();
        Log.d(TAG, "Thread wurde gestartet");

        Thread fib = new Thread(fibRunner());
        fib.start();

        new Thread(bewegeGegner1()).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Thread erzeugen
        Thread t = new Thread(bewegeGegner2());
        keepRunning = true;
        // Thread starten
        t.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepRunning = false;
    }

    private Runnable fibRunner() {
        return new Runnable() {

            @Override
            public void run() {
                int num = 20;
                int result = fib(num);
                Log.d(TAG, "fib(" + num + ") = " + result);
            }

            private int fib(int n) {
                switch (n) {
                    case 0:
                        return 0;
                    case 1:
                        return 1;
                    default:
                        Thread.yield();
                        return fib(n - 1) + fib(n - 2);
                }
            }
        };
    }

    private Runnable bewegeGegner1() {
        return new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.i(TAG, "bewege Gegner 1");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sleepTester()", e);
                    }
                }
            }
        };
    }

    private Runnable bewegeGegner2() {
        return new Runnable() {
            @Override
            public void run() {
                while (keepRunning) {
                    Log.i(TAG, "bewege Gegner 2");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sleepTester()", e);
                    }
                }
            }
        };
    }

}
