package com.thomaskuenneth.appinstalldemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExternalApplicationsAvailableReceiver extends BroadcastReceiver {
	
	private static final String TAG = ExternalApplicationsAvailableReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(intent
					.getAction())) {
				String[] packages = intent
						.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
				for (String pkg : packages) {
					Log.d(TAG, pkg);
				}
			}
		}
	}
}
