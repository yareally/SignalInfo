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

import static com.cc.signalinfo.util.SignalConstants.DEFAULT_TXT;

/**
 * @author Wes Lanning
 * @version 2012-12-21
 */
public final class SignalHelpers
{
    private SignalHelpers() {}

    /**
     * Get the intent that launches the additional radio settings screen
     *
     * @return the intent for the settings area
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
     * Checks to see if we have an rsrp and rsrq signal. If either
     * is the DEFAULT_TXT set for the rsrp/rsrq or null, then we assume
     * we can't calculate an estimated RSSI signal.
     *
     * @param rsrp - the RSRP LTE signal
     * @param rsrq - the RSRQ LTE signal
     * @return true if RSSI possible, false if not
     */
    public static boolean hasLteRssi(String rsrp, String rsrq)
    {
        return rsrp != null
            && rsrq != null
            && !DEFAULT_TXT.equals(rsrp)
            && !DEFAULT_TXT.equals(rsrq);
    }

    /**
     * Computes the LTE RSSI by what is most likely the default number of
     * channels on the LTE device (at least for Verizon).
     *
     * @param rsrp - the RSRP LTE signal
     * @param rsrq - the RSRQ LTE signal
     * @return the RSSI signal
     */
    public static int computeRssi(String rsrp, String rsrq)
    {
        return -(-17 - Integer.parseInt(rsrp) - Integer.parseInt(rsrq));
    }
}
