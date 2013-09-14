/*
 *
 * Copyright (c) 2013 Wes Lanning, http://codingcreation.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 */

package com.cc.signalinfo.util;

import android.telephony.SignalStrength;
import android.util.Log;
import com.cc.signalinfo.config.AppSetup;
import java.util.regex.Pattern;

/**
 * Wraps the raw signal data in order to filter
 * out invalid values as well as make the array compatible with ICS+
 * for shitty old devices from 2.3 and before
 * (though HSPA+ was not officially added until ICS, so technically 3.0 too).
 *
 * @author Wes Lanning
 * @version 2013-05-10
 */
public class SignalArrayWrapper
{
    // filter out any readings matching this regex as they're invalid
    private static final Pattern FILTER_SIGNAL  = Pattern.compile("0|-1|-?99|-?[1-9][0-9]{3,}");
    // fallback for really shitty devices were we can't find the gsm string in the signal
    private static final Pattern HAS_LETTERS    = Pattern.compile("[^\\d-\\s]+");
    // the normal system size for signal readings on ics
    private static final int     ICS_ARRAY_SIZE = 12;
    private static final Pattern SPACE_STR      = Pattern.compile(" ");
    // where in the signal string array is_gsm appears to remove it. It won't ever change so make it static
    private static       int     gsmPos         = -1;
    private String[] filteredArray;
    // keep a copy of the raw data for debugging purposes mostly
    private String   rawData;

    /**
     * Wraps the raw signal data array produced by the system in order
     * to return a uniform signal array that follows ICS+ standards.
     *
     * @param signalStrength - contains the raw signal info reported from the system
     */
    public SignalArrayWrapper(SignalStrength signalStrength)
    {
        rawData = signalStrength.toString();
        filteredArray = processSignalInfo(signalStrength);
    }

    /**
     * Constructor mainly for testing (passing in a mock object
     * for signals)
     *
     * @param signalArray - contains the raw signal info reported from the system
     */
    public SignalArrayWrapper(String[] signalArray)
    {
        rawData = java.util.Arrays.toString(signalArray);
        filteredArray = processSignalInfo(signalArray);
    }

    /**
     * Set the signal info the user sees.
     *
     * @param rawData - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private static String[] processSignalInfo(String[] rawData)
    {
        // ditch the text at the beginning and is_gsm at the end (because is_gsm is redundant with getNetworkType())
        int endPos = findIsGsmPos(rawData);
        String[] filteredData = new String[ICS_ARRAY_SIZE];

        for (int i = 1; i <= filteredData.length; ++i) {
            filteredData[i - 1] = i >= endPos || rawData[i] == null || FILTER_SIGNAL.matcher(rawData[i]).matches()
                ? AppSetup.DEFAULT_TXT
                : rawData[i];
        }
        Log.d("Raw Signal Array", java.util.Arrays.toString(rawData));
        Log.d("Filtered Signal Array", java.util.Arrays.toString(rawData));
        return filteredData;
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private static String[] processSignalInfo(SignalStrength signalStrength)
    {
        String[] splitSignals = SPACE_STR.split(signalStrength.toString());

        if (splitSignals.length < 5) { // meaning they don't use spaces like they should be using...
            // I could do this above with the space regex, but it's like 1-2 devices that somehow fuck this up and don't
            // want to punish everyone for their idiot developers.
            // could go and find what the most occuring non alpha-numeric char is, but if it isn't one of these, then screw em.
            splitSignals = signalStrength.toString().split("[ .,|:]+");
        }
        return processSignalInfo(splitSignals);
    }

    /**
     * Finds the position of the gsm|lte or cdma string in the array
     *
     * This is mainly for dealing with shitty old devices that don't have all the LTE API stuff.
     * Since those devices will have less fields in their raw data array, we must account for that.
     * Yes, it's not optimal to pretend they could have LTE by adding the values,
     * but fuck these devices. Especially since it's possible for 2.3 and 2.2 devices to have LTE,
     * which complicates things more. I prefer my sanity and letting them eat up a little more memory
     * on their craptastic device than running more checks later on.
     *
     * If you own one of these devices and are reading this, forgive the rage, but
     * things like this on Android frustrate the hell out of me and as a reader,
     * I assume you are developmentally inclined and can commiserate.
     *
     * Also, a happy developer is a good a good developer :)
     *
     * @param signalArray - the array to search
     * @return the position or -1 (not really possible) if not found
     */
    private static int findIsGsmPos(String[] signalArray)
    {
        // just assuming that some stupid OEM made the signalArray variable length -_-
        // really stupid if they did, but who knows with some of that crap I see in Android
        if (gsmPos == -1 || gsmPos > signalArray.length - 1) {
            // assume this device sucks so bad, it couldn't find the gsm position and try to compensate
            // by looking for anything not a number and assuming that's the value to stop on
            for (int i = signalArray.length - 1; i >= 0; --i) {
                if (HAS_LETTERS.matcher(signalArray[i]).matches()) {
                    gsmPos = i;
                    // return early to avoid going through all of the loop
                    return i;
                }
            }
            // assume this device sucks so bad, it couldn't find the gsm position and try to compensate
            // this shouldn't happen, but I wouldn't count on it with the way some Android devices are...
            gsmPos = gsmPos == -1 && signalArray.length - 1 > 0
                ? signalArray.length - 1
                : 0;
        }
        return gsmPos;
    }

    /**
     * Returns a copy (not a reference) of the signal array after
     * being reformatted to meet ICS+ expectations.
     *
     * @return the processed signal array in the form one expects in ICS+
     */
    public String[] getFilteredArray()
    {
        return Arrays.copyOf(filteredArray, filteredArray.length);
    }

    /**
     * Gets the pre-filtered data from the signal array.
     * Useful really only for debugging/testing purposes.
     *
     * @return raw signal data
     */
    public String getRawData()
    {
        return rawData;
    }
}
