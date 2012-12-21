package com.cc.signalinfo.util;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * User: admin
 * Date: 12/21/12
 * Time: 1:09 AM
 */
public final class SignalHelpers
{
    /**
     * Get the intent that launches the additional radio settings screen
     *
     * @return the intent for the settings area
     */
    public static Intent getAdditionalSettings()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        ComponentName showSettings = new ComponentName(
            "com.android.settings", "com.android.settings.TestingSettings");
        return intent.setComponent(showSettings);
    }

    /**
     * Prompts the user with a warning and makes them agree by pressing a
     * checkbox before they can continue onto the next screen.
     *
     * @param settings - used to verify if they previously agreed
     * @return true if user consents to the warning screen
     */
    public static boolean userConsent(SharedPreferences settings)
    {
        return !(!settings.contains(SignalConstants.PROMPT_SETTING)
            || !settings.getBoolean(SignalConstants.PROMPT_SETTING, false));
    }
}
