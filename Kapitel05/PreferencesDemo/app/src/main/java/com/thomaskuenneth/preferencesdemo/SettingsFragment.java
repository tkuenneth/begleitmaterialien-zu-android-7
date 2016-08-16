package com.thomaskuenneth.preferencesdemo;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Einstellungsseite aus XML-Datei laden
        addPreferencesFromResource(R.xml.preferences);
    }
}
