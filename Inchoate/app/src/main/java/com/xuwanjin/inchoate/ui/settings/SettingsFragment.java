package com.xuwanjin.inchoate.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.xuwanjin.inchoate.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);
    }

}
