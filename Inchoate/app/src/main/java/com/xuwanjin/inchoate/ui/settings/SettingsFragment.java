package com.xuwanjin.inchoate.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.xuwanjin.inchoate.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_LOG_IN = "log_in_key";
    public static final String KEY_DARK_MODE_PREFERENCE = "dark_mode_preference_key";
    public static final String KEY_TEXT_SIZE_CHANGE = "text_size_change_key";
    public static final String KEY_AUTO_DOWNLOAD_DOWNLOAD = "auto_download_download_key";
    public static final String KEY_BACKGROUND_DOWNLOADS = "background_downloads_key";
    public static final String KEY_NOTIFICATIONS = "notifications_key";
    public static final String KEY_CONTACT_US = "contact_us_key";
    public static final String KEY_ABOUT = "about_key";
    Preference logInPreference;
    Preference textSizeChangePreference;
    Preference autoDownloadPreference;
    Preference backgroundDownloadsPreference;
    Preference notificationPreference;
    Preference contactUsPreference;
    Preference aboutPreference;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);
        logInPreference = findPreference(KEY_DARK_MODE_PREFERENCE);
        textSizeChangePreference = findPreference(KEY_TEXT_SIZE_CHANGE);
        autoDownloadPreference = findPreference(KEY_AUTO_DOWNLOAD_DOWNLOAD);
        backgroundDownloadsPreference = findPreference(KEY_BACKGROUND_DOWNLOADS);
        notificationPreference = findPreference(KEY_NOTIFICATIONS);
        contactUsPreference = findPreference(KEY_CONTACT_US);
        aboutPreference = findPreference(KEY_ABOUT);
    }

}
