package com.thomaskuenneth.servicedemo2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocalService extends Service {

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public int fakultaet(int n) {
		if (n <= 0) {
			return 1;
		}
		return n * fakultaet(n - 1);
	}
}