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
    private static final String KEY_FORGIVENESS = "forgiveness";

    /**
     * Set desired microphone amplitude response trigger sensitivity.
     *
     * Should be controlled by {@link SettingsActivity}.
     *
     * @param sensitivity int from 0 to 100 (inclusive)
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

    /**
     * Roughly equivalent to "how long must a noise be consistently too loud before we care?"
     *
     * Should be controlled by {@link SettingsActivity}.
     *
     * @param forgiveness int from 0 to 100 (inclusive)
     */
    public static void setForgiveness(int forgiveness) {
        getSettings().edit().putInt(KEY_FORGIVENESS, forgiveness).apply();
    }

    public static int getForgiveness() {
        return getSettings().getInt(KEY_FORGIVENESS, 20);
    }

    private static SharedPreferences getSettings() {
        return HushApp.getAppContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
