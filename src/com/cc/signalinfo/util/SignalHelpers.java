/*
Copyright (c) 2012 Wes Lanning, http://codingcreation.com

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

http://www.opensource.org/licenses/mit-license.php
*/

package com.cc.signalinfo.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.cc.signalinfo.config.SignalConstants;

import java.util.Set;

/**
 * The type Signal helpers.
 * @author Wes Lanning
 * @version 2012 -12-21
 */
public final class SignalHelpers
{
    private static final String TAG = SignalHelpers.class.getSimpleName();

    private SignalHelpers() {}

    /**
     * Get the intent that launches the additional radio settings screen
     *
     * @return the intent for the settings area
     * @throws SecurityException the security exception
     */
    public static Intent getAdditionalSettings() throws SecurityException
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

    /**
     * Has lte api.
     *
     * @param settings the settings
     * @return the boolean
     */
    public static boolean hasLteApi(SharedPreferences settings)
    {
        return !(!settings.contains(SignalConstants.OLD_FUCKING_DEVICE)
            || !settings.getBoolean(SignalConstants.OLD_FUCKING_DEVICE, false));
    }

    /**
     * Gets shared preferences.
     *
     * @param activity the activity
     * @return the shared preferences
     */
    public static SharedPreferences getSharedPreferences(Activity activity)
    {
        return activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * Add shared preference.
     *
     * @param activity the activity
     * @param settingName the setting name
     * @param settingValue the setting value
     * @return the boolean
     */
    public static boolean addSharedPreference(Activity activity, String settingName, Object settingValue)
    {
        SharedPreferences settings = getSharedPreferences(activity);
        SharedPreferences.Editor editor = settings.edit();

        try {
            if (settingValue instanceof Boolean) {
                return editor.putBoolean(settingName, (Boolean) settingValue).commit();
            }
            else if (settingValue instanceof String) {
                return editor.putString(settingName, (String) settingValue).commit();
            }
            else if (settingValue instanceof Integer) {
                return editor.putInt(settingName, (Integer) settingValue).commit();
            }
            else if (settingValue instanceof Set) { // requires android 3.0 or higher
                return editor.putStringSet(settingName, (Set<String>) settingValue).commit();
            }
            else if (settingValue instanceof Float) {
                return editor.putFloat(settingName, (Float) settingValue).commit();
            }
            else if (settingValue instanceof Long) {
                return editor.putLong(settingName, (Long) settingValue).commit();
            }
        } catch (ClassCastException e) {
            Log.e(TAG, String.format("Cannot convert %s", settingValue.toString()), e);
        }
        return false;
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static boolean getPreference(Activity activity, String preferenceName, boolean defaultReturnValue)
    {
        return getSharedPreferences(activity).getBoolean(preferenceName, defaultReturnValue);
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static String getPreference(Activity activity, String preferenceName, String defaultReturnValue)
    {
        return getSharedPreferences(activity).getString(preferenceName, defaultReturnValue);
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static int getPreference(Activity activity, String preferenceName, int defaultReturnValue)
    {
        return getSharedPreferences(activity).getInt(preferenceName, defaultReturnValue);
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static float getPreference(Activity activity, String preferenceName, float defaultReturnValue)
    {
        return getSharedPreferences(activity).getFloat(preferenceName, defaultReturnValue);
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static long getPreference(Activity activity, String preferenceName, long defaultReturnValue)
    {
        return getSharedPreferences(activity).getLong(preferenceName, defaultReturnValue);
    }

    /**
     * Gets preference.
     *
     * @param activity the activity
     * @param preferenceName the preference name
     * @param defaultReturnValue the default return value
     * @return the preference
     */
    public static Set<String> getPreference(Activity activity, String preferenceName, Set<String> defaultReturnValue)
    {
        return getSharedPreferences(activity).getStringSet(preferenceName, defaultReturnValue);
    }
}
