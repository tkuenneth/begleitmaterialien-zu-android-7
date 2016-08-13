package com.thomaskuenneth.fibonaccidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FibonacciDemoActivity extends Activity {

    private static final String TAG = FibonacciDemoActivity.class
            .getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG, "fib(5) = " + fib(5));
    }

    private int fib(int n) {
        Log.i(TAG, "n=" + n);
        int fib;
        switch (n) {
            case 0:
                fib = 0;
                break;
            case 1:
                fib = 1;
                break;
            default:
                fib = fib(n - 1) + fib(n - 2);
        }
        return fib;
    }
}
