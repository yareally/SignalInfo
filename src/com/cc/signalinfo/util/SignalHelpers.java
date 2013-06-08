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

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import com.cc.signalinfo.config.AppSetup;

/**
 * The type Signal helpers.
 *
 * @author Wes Lanning
 * @version 2012-12-21
 */
public final class SignalHelpers
{
    private static final String TAG = SignalHelpers.class.getSimpleName();

    /**
     * Get the intent that launches the additional radio settings screen
     *
     * @return the intent for the settings area
     *
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
        return !(!settings.contains(AppSetup.PROMPT_SETTING)
            || !settings.getBoolean(AppSetup.PROMPT_SETTING, false));
    }

    /**
     * Has lte api.
     *
     * @param settings the settings
     * @return the boolean
     */
    public static boolean hasLteApi(SharedPreferences settings)
    {
        return !(!settings.contains(AppSetup.OLD_FUCKING_DEVICE)
            || !settings.getBoolean(AppSetup.OLD_FUCKING_DEVICE, false));
    }

    private SignalHelpers() {}
}
