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
    public static final String KEY_BOOKMARK_GROUP_POLICY = "bookmark_group_policy";
    public static final String KEY_NOTIFICATIONS = "notifications_key";
    public static final String KEY_CONTACT_US = "contact_us_key";
    public static final String KEY_ABOUT = "about_key";
    Preference mLogInPreference;
    Preference mDarkModePreference;
    Preference mTextSizeChangePreference;
    Preference mAutoDownloadPreference;
    Preference mBackgroundDownloadsPreference;
    Preference mBookmarkGroupPolicyPreference;
    Preference mNotificationPreference;
    Preference mContactUsPreference;
    Preference mAboutPreference;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);
        mLogInPreference = findPreference(KEY_LOG_IN);
        mDarkModePreference = findPreference(KEY_DARK_MODE_PREFERENCE);
        mTextSizeChangePreference = findPreference(KEY_TEXT_SIZE_CHANGE);
        mAutoDownloadPreference = findPreference(KEY_AUTO_DOWNLOAD_DOWNLOAD);
        mBackgroundDownloadsPreference = findPreference(KEY_BACKGROUND_DOWNLOADS);
        mBookmarkGroupPolicyPreference = findPreference(KEY_BOOKMARK_GROUP_POLICY);
        mNotificationPreference = findPreference(KEY_NOTIFICATIONS);
        mContactUsPreference = findPreference(KEY_CONTACT_US);
        mAboutPreference = findPreference(KEY_ABOUT);
    }

}
