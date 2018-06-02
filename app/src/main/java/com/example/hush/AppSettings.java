package com.example.hush;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Convenience wrapper for {@link SharedPreferences} that only exposes properties the app should
 * care about.
 */
class AppSettings {

    private static final String PREF_NAME = "app_settings";
    private static final String KEY_SENSITIVITY = "sensitivity";

    /**
     * Set desired microphone amplitude response trigger sensitivity.
     *
     * Should be controlled by the {@link android.widget.SeekBar} in {@link SettingsActivity},
     * which will set this to a value between 0 and 100 (inclusive)
     */
    public static void setSensitivity(int sensitivity) {
        getSettings().edit().putInt(KEY_SENSITIVITY, sensitivity).apply();
    }

    /**
     * Get desired microphone amplitude response trigger sensitivity.
     * @return an int between 0 and 100 (inclusive)
     */
    public static int getSensitivity() {
        return getSettings().getInt(KEY_SENSITIVITY, 50);
    }

    private static SharedPreferences getSettings() {
        return HushApp.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
