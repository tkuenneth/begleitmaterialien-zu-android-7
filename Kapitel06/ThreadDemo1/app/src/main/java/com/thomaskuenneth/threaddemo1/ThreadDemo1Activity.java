package com.thomaskuenneth.threaddemo1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class ThreadDemo1Activity extends Activity {

    private static final String TAG =
            ThreadDemo1Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run()-Methode wurde aufgerufen");
            }
        };
        Thread t = new Thread(r);
        // Thread t = new Thread(fibRunner());
        // Thread t = new Thread(sleepTester());
        t.start();
        Log.d(TAG, "Thread wurde gestartet");
        Log.d(TAG, Thread.currentThread().getName());
    }

    private Runnable fibRunner() {
        Runnable r = new Runnable() {

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
        return r;
    }

    private Runnable sleepTester() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d(TAG, "bewege Gegner");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // Unterbrechungen ignorieren
                    }
                }
            }
        };
        return r;
    }
}
