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
 * /
 */

package com.cc.signalinfo.libs;

import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.signals.CdmaInfo;
import com.cc.signalinfo.signals.GsmInfo;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.signals.LteInfo;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class SignalData
{
    private static final Pattern SPACE_STR     = Pattern.compile(" ");
    // filter out any readings matching this regex as they're invalid
    private static final Pattern FILTER_SIGNAL = Pattern.compile("0|-1|-?99|-?[1-9][0-9]{3,}");
    // assume this is some 2.3 or before device until otherwise
    private              int     oldDevice     = 1;
    // where in the signal string array is_gsm appears to remove it
    private              int     gsmPos        = -1;
    private Map<NetworkType, ISignal> networkMap;

    public SignalData(SignalStrength signalStrength, TelephonyManager tm)
    {
        String[] rawSignalData = processSignalInfo(signalStrength);
        networkMap = createSignalDataMap(tm, rawSignalData);
    }

    public SignalData(String[] signalStrength, TelephonyManager tm)
    {
        String[] rawSignalData = processSignalInfo(signalStrength);
        networkMap = createSignalDataMap(tm, rawSignalData);
    }

    /**
     * Initialize the network map that will hold all the various signal readings
     *
     * @param tm - dependency for the network map
     * @return the created map, empty other than the signal container maps (for gsm, lte, etc)
     */
    public static Map<NetworkType, ISignal> initNetworkMap(TelephonyManager tm)
    {
        Map<NetworkType, ISignal> networkMap = new EnumMap<>(NetworkType.class);
        networkMap.put(NetworkType.GSM, new GsmInfo(tm));
        networkMap.put(NetworkType.CDMA, new CdmaInfo(tm));
        networkMap.put(NetworkType.LTE, new LteInfo(tm));
        return networkMap;
    }

    /**
     * Deal with shitty old devices that don't have all the LTE API stuff.
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
     * @param signalArray - they signal data pulled from the device.
     * @return a slightly larger, but more sane array that looks like one from ICS or greater
     */
    private static String[] legacyWorkarounds(String[] signalArray)
    {
        // will give nulls (on new array buckets), so have to deal with that later in return method
        signalArray = Arrays.copyOf(signalArray, 13);
        return signalArray;
    }

    /**
     * Removes any crap that might show weird numbers because the phone does not support
     * some reading or avoids causing an exception by removing it.
     *
     * @param data - data to filter
     * @param tm - dependency for the network map
     * @return filtered data with "n/a" instead of the bad value
     */
    public final Map<NetworkType, ISignal> createSignalDataMap(TelephonyManager tm, String[] data)
    {
        Map<NetworkType, ISignal> networkMap = initNetworkMap(tm);
        Signal[] values = Signal.values();

        for (int i = 0; i < values.length; ++i) {
            String signalValue = i >= data.length || data[i] == null || FILTER_SIGNAL.matcher(data[i]).matches()
                ? AppSetup.DEFAULT_TXT
                : data[i];

            if (signalValue.toLowerCase(Locale.ENGLISH).contains("gsm")
                || signalValue.toLowerCase(Locale.ENGLISH).contains("cdma")) {
                gsmPos = i;
            }
            networkMap.get(values[i].type()).addSignalValue(values[i], signalValue);
        }
        Log.d("Signal Map CDMA: ", networkMap.get(NetworkType.CDMA).getSignals().toString());
        Log.d("Signal Map GSM: ", networkMap.get(NetworkType.GSM).getSignals().toString());
        Log.d("Signal Map LTE: ", networkMap.get(NetworkType.LTE).getSignals().toString());
        return networkMap;
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalArray - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    public final String[] processSignalInfo(String[] signalArray)
    {
        // ditch the text at the beginning and is_gsm at the end (because is_gsm is redundant with getNetworkType())
        int endPos = findIsGsmPos(signalArray);
        signalArray = Arrays.copyOfRange(signalArray, 1, endPos);

        if (signalArray.length < 13) {
            signalArray = legacyWorkarounds(signalArray); // 2.2 or 2.3 device >:(
        }
        else {
            oldDevice = 0; // ICS+ device, yay
        }
        Log.d("Signal Array", Arrays.toString(signalArray));
        return signalArray;
    }

    public final String[] processSignalInfo(SignalStrength signalStrength)
    {
        return processSignalInfo(SPACE_STR.split(signalStrength.toString()));
    }

    /**
     * Do we already have signal data entered from the system?
     *
     * @return true if we already have signal data collected.
     */
    public boolean hasData()
    {
        return !networkMap.isEmpty();
    }

    public Map<NetworkType, ISignal> getNetworkMap()
    {
        return Collections.unmodifiableMap(networkMap);
    }

    /**
     * Super old devices (2.3 and before), yay
     *
     * @return true if this is an old device that requires workarounds
     */
    public int legacyDevice()
    {
        return oldDevice;
    }

    /**
     * Finds the position of the gsm|lte or cdma string in the array
     *
     * @param signalArray - the array to search
     * @return the position or -1 (not really possible) if not found
     */
    private int findIsGsmPos(String[] signalArray)
    {
        // if we haven't already found it before, find it now
        if (gsmPos == -1) {
            for (int i = signalArray.length - 1; i >= 0; --i) {
                if (signalArray[i].toLowerCase(Locale.ENGLISH).contains("gsm")
                    || signalArray[i].toLowerCase(Locale.ENGLISH).contains("cdma")) {
                    gsmPos = i;
                    // return early to avoid going through all of the loop
                    return i;
                }
            }
        }
        return gsmPos;
    }
}
