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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.telephony.SignalStrength;
import android.util.Log;
import android.widget.TextView;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.config.SignalConstants;
import com.cc.signalinfo.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class SignalData
{
    // TODO: use the stuff in the signals package now that it's implemented.

    private static final Pattern SPACE_STR     = Pattern.compile(" ");
    private static final Pattern FILTER_SIGNAL = Pattern.compile("-1|-?99|-?[1-9][0-9]{3,}");

    private Map<Signal, String> signalData = new EnumMap<Signal, String>(Signal.class);
    private Context context;
    private Map<Signal, TextView>     signalTextViewMap = new EnumMap<Signal, TextView>(Signal.class);
    private Map<NetworkType, ISignal> networkMap        = new EnumMap<NetworkType, ISignal>(NetworkType.class);

    public SignalData(Context context, SignalStrength signalStrength)
    {
        this.context = context;
        this.signalData = processSignalInfo(signalStrength);
    }

    /**
     * Removes any crap that might show weird numbers because the phone does not support
     * some reading or avoids causing an exception by removing it.
     *
     * @param data - data to filter
     * @return filtered data with "n/a" instead of the bad value
     */
    public static Map<Signal, String> filterSignalData(String[] data)
    {
        // TODO: shitty old devices without lte in their api, I should account for by skipping over lte values (because network type gets set to lte_sig_strength for them)
        // example (on 2.3 gsm phone): {GSM_SIG_STRENGTH=7, GSM_BIT_ERROR=N/A, CDMA_RSSI=N/A, CDMA_ECIO=N/A, EVDO_RSSI=N/A, EVDO_ECIO=N/A, EVDO_SNR=N/A, LTE_SIG_STRENGTH=gsm}
        Map<Signal, String> signalData = new EnumMap<Signal, String>(Signal.class);
        Signal[] values = Signal.values();

        for (int i = 0; i < data.length; ++i) {
            String signalValue = FILTER_SIGNAL.matcher(data[i]).matches()
                ? SignalConstants.DEFAULT_TXT
                : data[i];
            signalData.put(values[i], signalValue);
        }
        String lteRssi = SignalConstants.DEFAULT_TXT;

        if (hasLteRssi(signalData.get(Signal.LTE_RSRP), signalData.get(Signal.LTE_RSRQ))) {
            lteRssi = String.valueOf(
                computeRssi(
                    signalData.get(Signal.LTE_RSRP),
                    signalData.get(Signal.LTE_RSRQ)));
        }
        signalData.put(Signal.LTE_RSSI, String.valueOf(lteRssi));
        return signalData;
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private static Map<Signal, String> processSignalInfo(SignalStrength signalStrength)
    {
        String[] signalArray = SPACE_STR.split(signalStrength.toString());
        signalArray = Arrays.copyOfRange(signalArray, 1, signalArray.length);
        Map<Signal, String> sigInfo = filterSignalData(signalArray);
        Log.d("Signal Array", sigInfo.toString());
        return sigInfo;
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
        if (!StringUtils.isNullOrEmpty(rsrp)
            && !StringUtils.isNullOrEmpty(rsrq)
            && !SignalConstants.DEFAULT_TXT.equals(rsrp)
            && !SignalConstants.DEFAULT_TXT.equals(rsrq)) {
            return true;
        }
        else {
            return false;
        }
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

    /**
     * Gets the TextViews that map to the signal info data in the code for binding.
     *
     * @param sigInfoIds - the array containing the IDs to the TextView resources
     * @param refreshMap - should we recreate the map or reuse it? (in case we some reason added some, somehow)
     * @return map of the Signal data enumeration types (keys) and corresponding TextViews (values)
     */
    public Map<Signal, TextView> getSignalTextViewMap(TypedArray sigInfoIds, boolean refreshMap)
    {
        // no reason to do this over and over if it's already filled (we keep the same text stuff
        if (signalTextViewMap.isEmpty() || refreshMap) {
            Signal[] values = Signal.values();

            for (int i = 1; i <= sigInfoIds.length(); ++i) {
                int id = sigInfoIds.getResourceId(i, -1);

                if (id != -1) {
                    TextView currentView = (TextView) ((Activity) context).findViewById(id);
                    signalTextViewMap.put(values[i], currentView);
                }
            }
        }
        return Collections.unmodifiableMap(signalTextViewMap);
    }

    /**
     * Gets the TextViews that map to the signal info data in the code for binding.
     *
     * @param sigInfoIds - the array containing the IDs to the TextView resources
     * @return map of the Signal data enumeration types (keys) and corresponding TextViews (values)
     */
    public Map<Signal, TextView> getSignalTextViewMap(TypedArray sigInfoIds)
    {
        return getSignalTextViewMap(sigInfoIds, false);
    }

    /**
     * Do we already have signal data entered from the system?
     *
     * @return true if we already have signal data collected.
     */
    public boolean hasData()
    {
        return !signalData.isEmpty();
    }

    /**
     * Gets the last recorded instance of the signal data map
     *
     * @return signal data as a map
     */
    public Map<Signal, String> getData()
    {
        return Collections.unmodifiableMap(signalData);
    }
}
