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

package com.cc.signalinfo;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cc.signalinfo.R.id;
import com.cc.signalinfo.R.layout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
public class SignalInfo extends FragmentActivity // implements OnClickListener
{
    private static final Pattern              SPACE_STR          = Pattern.compile(" ");
    private static final String               SD_DIR             = "/Android/data/signalinfo";
    private static final String               SS_SUFFIX          = "signal-state.png";
    private static final int                  GSM_SIG_STRENGTH   = 1;
    private static final int                  GSM_BIT_ERROR      = 2;
    private static final int                  CDMA_SIGNAL        = 3;
    private static final int                  CDMA_ECIO          = 4;
    private static final int                  EVDO_SIGNAL        = 5;
    private static final int                  EVDO_ECIO          = 6;
    private static final int                  EVDO_SNR           = 7;
    private static final int                  LTE_SIG_STRENGTH   = 8;
    private static final int                  LTE_RSRP           = 9;
    private static final int                  LTE_RSRQ           = 10;
    private static final int                  LTE_SNR            = 11;
    private static final int                  LTE_CQI            = 12;
    private static final int                  IS_GSM             = 13;
    private static final int                  LTE_RSSI           = 14;
    private static final String               DEFAULT_TXT        = "N/A";
    private static final int                  MAX_SIGNAL_ENTRIES = 14;
    private final        String               TAG                = getClass().getSimpleName();
    private              MyPhoneStateListener listen             = null;
    private              TelephonyManager     tm                 = null;

    private static int computeRssi(String rsrp, String rsrq)
    {
        return -17 - Integer.parseInt(rsrp) - Integer.parseInt(rsrq);
    }

    private static String[] formatSignalData(String... data)
    {
        for (int i = 0; i < data.length; ++i) {
            data[i] = "-1".equals(data[i])
                || "99".equals(data[i])
                || "1000".compareTo(data[i]) == -1
                ? "N/A"
                : data[i];
        }
        return data;
    }

    /**
     * Initialize the app.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
/*        AdView ad = (AdView) findViewById(id.adView);
        ad.loadAd(new AdRequest());*/
        listen = new MyPhoneStateListener();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        tm.listen(listen, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        setPhoneInfo();
    }

    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private void setPhoneInfo()
    {
        TextView t = (TextView) findViewById(id.phoneName);
        t.setText(Build.MANUFACTURER + ' ' + Build.MODEL);

        t = (TextView) findViewById(id.phoneModel);
        t.setText(Build.PRODUCT + '/' + Build.DEVICE + " (" + Build.ID + ") ");

        t = (TextView) findViewById(id.androidVersion);
        t.setText(Build.VERSION.RELEASE + " (API version " + Build.VERSION.SDK_INT + ')');

        t = (TextView) findViewById(id.carrierName);
        t.setText(tm.getNetworkOperatorName());

        t = (TextView) findViewById(id.buildHost);
        t.setText(Build.HOST);
    }

    /**
     * Stop recording when screen is not in the front.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        tm.listen(listen, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * Start recording when the screen is on again.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        tm.listen(listen, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private void setSignalInfo(SignalStrength signalStrength)
    {
        Log.d(TAG, "formatting sig str");
        String[] sigInfo = formatSignalData(SPACE_STR.split(signalStrength.toString()));

        Log.d("Signal Array", Arrays.toString(sigInfo));
        Map<Integer, TextView> signalDataMap = getSignalDataMap();

        for (Map.Entry<Integer, TextView> data : signalDataMap.entrySet()) {
            // TODO: maybe use an adapter of some sort instead of this (ListAdapter maybe?)
            TextView currentTextView = data.getValue();

            try {
                String sigValue;

                if (data.getKey() == LTE_RSSI) {
                    sigValue = DEFAULT_TXT.equals(sigInfo[LTE_RSRP]) || DEFAULT_TXT.equals(sigInfo[LTE_RSRQ])
                        ? DEFAULT_TXT
                        : "-" + computeRssi(sigInfo[LTE_RSRP], sigInfo[LTE_RSRQ]);
                }
                else {
                    sigValue = sigInfo[data.getKey()];
                }
                Log.d(TAG, "sigValue: " + sigValue);

                if (!sigValue.equals(DEFAULT_TXT)) {
                    String db = "";
                    if (data.getKey() != IS_GSM) {
                        db = " db";
                    }
                    Log.d(TAG, "sigvalue before setting: " + sigValue + db);
                    currentTextView.setText(sigValue + db);
                }
            }
            catch (Resources.NotFoundException ignored) {
                currentTextView.setText(DEFAULT_TXT);
            }
        }
    }

    /**
     * Get the TextView that matches with the signal data
     * value and store both in a map entry. data value is tied to the
     * order it would be returned in the toString() method to get
     * all data from SignalStrength.
     *
     * @return - the mapped TextViews to their signal data key
     */
    private Map<Integer, TextView> getSignalDataMap()
    {
        LinearLayout layout = (LinearLayout) this.findViewById(id.main);
        Pattern uscore = Pattern.compile("_");
        Map<Integer, TextView> signalData = new HashMap<Integer, TextView>(28);

        for (int i = 1; i <= MAX_SIGNAL_ENTRIES; ++i) {
            try {
                TextView currentView = (TextView) layout.findViewWithTag(String.valueOf(i));
                if (currentView != null) {
                    Log.d(TAG, "Current text view: "
                        + getResources().getResourceEntryName(currentView.getId())
                        + " id: " + currentView.getId());
                }
                else {
                    Log.d(TAG, "Current Text View retrieved was null");
                    continue;
                }
                String[] childName = uscore.split(
                    getResources().getResourceEntryName(currentView.getId()));


                if (childName.length > 1) {
                    signalData.put(Integer.parseInt(childName[1]), currentView);
                }
                Log.d(TAG, "array data: " + Arrays.toString(childName));
                Log.d(TAG, "array data len: " + childName.length);
            }
            catch (Resources.NotFoundException ignored) {
                Log.e(TAG, "Could not parse signal textviews");
            }
        }
        Log.d(TAG, "Returning signal data: " + signalData.size());
        return signalData;
    }

    /**
     * Private helper class to listen for network signal changes.
     */
    private class MyPhoneStateListener extends PhoneStateListener
    {
        /**
         * Get the Signal strength from the provider, each time there is an update
         *
         * @param signalStrength - has all the useful signal stuff in it.
         */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);

            if (signalStrength != null) {
                setSignalInfo(signalStrength);
                Log.d(TAG, "getting sig strength");
                Log.d(TAG, signalStrength.toString());
            }
        }
    }
}
