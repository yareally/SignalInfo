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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cc.signalinfo.R.id;
import com.cc.signalinfo.R.layout;
import com.cc.signalinfo.util.SignalHelpers;
import com.cc.signalinfo.util.StringUtils;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.cc.signalinfo.util.SignalConstants.*;
// ↑ Because the over verbosity on the constants will probably give me brain damage...

/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
@SuppressWarnings({"RedundantFieldInitialization", "ReuseOfLocalVariable"})
public class MainActivity extends SherlockFragmentActivity implements View.OnClickListener
{
    private final        String           TAG           = getClass().getSimpleName();
    private              String[]         sigInfoTitles = null;
    private              TypedArray       sigInfoIds    = null;
    private              SignalListener   listener      = null;
    private              TelephonyManager tm            = null;
    private              ActionBar        actionBar     = null;

    /**
     * Initialize the app.
     *
     * @param savedInstanceState - umm... the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        setContentView(layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        formatFooter();

        listener = new SignalListener();
        sigInfoTitles = getResources().getStringArray(R.array.sigInfoTitles);
        sigInfoIds = getResources().obtainTypedArray(R.array.sigInfoIds);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        findViewById(id.additionalInfo).setOnClickListener(this);
        setPhoneInfo();

        AdView ad = (AdView) findViewById(id.adView);
        ad.loadAd(new AdRequest());
    }

    /**
     * Shows additional radio settings contained in the Android OS.
     *
     * @param view - button that shows the settings.
     */
    @Override
    public void onClick(View view)
    {
        if (SignalHelpers.userConsent(getPreferences(Context.MODE_PRIVATE))) {
            try {
                startActivity(SignalHelpers.getAdditionalSettings());
            } catch (ActivityNotFoundException ignored) {
                Toast.makeText(this, getString(R.string.noAdditionalSettingSupport), Toast.LENGTH_LONG).show();
            } catch (SecurityException ignored) {
                Toast.makeText(this, getString(R.string.additionalSettingPermDenied), Toast.LENGTH_LONG).show();
            }
        }
        else {
            new WarningDialogFragment().show(getSupportFragmentManager(), "Warning");
        }
    }

    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private void setPhoneInfo()
    {
        TextView t = (TextView) findViewById(id.phoneName);
        t.setText(String.format("%s %s", Build.MANUFACTURER, Build.MODEL));

        t = (TextView) findViewById(id.phoneModel);
        t.setText(String.format("%s/%s (%s) ", Build.PRODUCT, Build.DEVICE, Build.ID));

        t = (TextView) findViewById(id.androidVersion);
        t.setText(String.format("%s (API version %d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));

        t = (TextView) findViewById(id.carrierName);
        t.setText(tm.getNetworkOperatorName());

        t = (TextView) findViewById(id.buildHost);
        t.setText(Build.HOST);
    }

    /**
     * Formats the page footer with in the following format:
     * ©YEAR codingcreation.com | v. x.xx
     */
    private void formatFooter()
    {
        try {
            ((TextView) findViewById(id.copyright))
                .setText(String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance().get(Calendar.YEAR),
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException ignored) {
            Log.wtf(TAG, "Could not display app version number!");
        }
    }

    /**
     * Stop recording when screen is not in the front.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * Start recording when the screen is on again.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private void setSignalInfo(SignalStrength signalStrength)
    {
        SignalData signalData = new SignalData(this, signalStrength);

        if (signalData.hasData()) {
            displaySignalInfo(signalData);
        }
        else {
            Toast.makeText(this, getString(R.string.deviceNotSupported), Toast.LENGTH_LONG).show();
        }
    }

    private void displaySignalInfo(SignalData signalData)
    {
        Map<Signal, String> sigInfo = signalData.getData();
        Map<Signal, TextView> signalDataMap = signalData.getSignalTextViewMap(sigInfoIds);
        String unit = getString(R.string.unitMeasure);

        for (Map.Entry<Signal, TextView> data : signalDataMap.entrySet()) {
            // TODO: maybe use an adapter of some sort instead of this (ListAdapter maybe?)
            TextView currentTextView = data.getValue();

            try {
                String sigValue = sigInfo.get(data.getKey());

                if (!StringUtils.isNullOrEmpty(sigValue) && !sigValue.equals(DEFAULT_TXT)) {
                    String db = data.getKey() == Signal.IS_GSM
                        ? ""
                        : String.format(" %s", unit);
                    currentTextView.setText(sigValue + db);
                }
            } catch (Resources.NotFoundException ignored) {
                currentTextView.setText(DEFAULT_TXT);
            }
        }

    }

    /**
     * Private helper class to listener for network signal changes.
     */
    private class SignalListener extends PhoneStateListener
    {
        /* TODO: think about making this a singleton if ever needed outside of just this file
           so we're not creating a bunch of telephony listeners
        */

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
