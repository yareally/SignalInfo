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

package com.cc.signalinfo.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cc.signalinfo.BuildConfig;
import com.cc.signalinfo.R;
import com.cc.signalinfo.dialogs.WarningDialogFragment;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.fragments.SettingsFragment;
import com.cc.signalinfo.listeners.SignalListener;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.signals.SignalInfo;
import com.cc.signalinfo.util.SignalArrayWrapper;
import com.cc.signalinfo.util.SignalHelpers;
import com.cc.signalinfo.util.SignalMapWrapper;
import com.cc.signalinfo.util.StringUtils;
import com.commonsware.cwac.loaderex.acl.SharedPreferencesLoader;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.*;

import static com.cc.signalinfo.config.AppSetup.DEFAULT_TXT;
import static com.cc.signalinfo.config.AppSetup.enableStrictMode;
// ? Because the over verbosity on the constants will probably give me brain damage...

/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
@SuppressWarnings({"RedundantFieldInitialization", "ReuseOfLocalVariable", "LawOfDemeter"})
public class MainActivity extends SherlockFragmentActivity implements View.OnClickListener, SignalListener.UpdateSignal, LoaderManager.LoaderCallbacks<SharedPreferences>
{
    private static final String                TAG               = MainActivity.class.getSimpleName();
    private              ActionBar             actionBar         = null;
    private              boolean               dbOnly            = false;
    private              boolean               enableDebug       = false;
    private              String[]              filteredSignals   = null;
    private              boolean               fudgeSignal       = true;
    private              SignalListener        listener          = null;
    private              SharedPreferences     preferences       = null;
    private              TypedArray            sigInfoIds        = null;
    private              Map<Signal, TextView> signalTextViewMap = new EnumMap<>(Signal.class);
    private              TelephonyManager      tm                = null;

    /**
     * Initialize the app.
     *
     * @param savedInstanceState - umm... the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        enableStrictMode();
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        SignalListener listener = new SignalListener(this);
        sigInfoIds = getResources().obtainTypedArray(R.array.sigInfoIds);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        getSupportLoaderManager().initLoader(0, null, this);

        setContentView(R.layout.main);
        findViewById(R.id.additionalInfo).setOnClickListener(this);
        setPhoneInfo();
        formatFooter();

        if (!BuildConfig.DEBUG) {
            AdView ad = (AdView) findViewById(R.id.adView);
            ad.loadAd(new AdRequest());
        }
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
            } catch (SecurityException | ActivityNotFoundException ignored) {
                Toast.makeText(this,
                    getString(R.string.noAdditionalSettingSupport),
                    Toast.LENGTH_LONG).show();
            }
        }
        else {
            new WarningDialogFragment().show(getSupportFragmentManager(), "Warning");
        }
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
        // no reason to do this over and over if it's already filled (we keep the same text stuff)
        if (signalTextViewMap.isEmpty() || refreshMap) {
            Signal[] values = Signal.values();

            for (int i = 0; i < sigInfoIds.length(); ++i) {
                int id = sigInfoIds.getResourceId(i, -1);

                if (id != -1) {
                    TextView currentView = (TextView) findViewById(id);
                    signalTextViewMap[values[i]] = currentView;
                }
            }
        }
        return Collections.unmodifiableMap(signalTextViewMap);
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    @Override
    public void setData(SignalArrayWrapper signalStrength)
    {
        if (signalStrength == null) {
            return;
        }
        filteredSignals = signalStrength.getFilteredArray();
        displayDebugInfo(signalStrength);
        displaySignalInfo(filteredSignals);
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param menu - the action bar menu
     * @return true on created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param item - item to populate
     * @return true on create
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.preferences:
                loadPrefsScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (preferences != null) {
            setPreferences(preferences);
            displaySignalInfo(filteredSignals);
        }
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * Create a new shared preferences loader when there isn't one or
     * one is no longer instantiated
     *
     * @param i - the id for the loader we want (typically 0, but not always)
     * @param bundle - any extra stuff to fetch (probably not used)
     * @return the SharedPreferencesLoader
     */
    @Override
    public Loader<SharedPreferences> onCreateLoader(int i, Bundle bundle)
    {
        return new SharedPreferencesLoader(this);
    }

    /**
     * After the preferences have been loaded, do the stuff here
     *
     * @param sharedPreferencesLoader - loader for the preferences
     * @param sharedPreferences - all the previously saved user preferences and such
     */
    @Override
    public void onLoadFinished(Loader<SharedPreferences> sharedPreferencesLoader, SharedPreferences sharedPreferences)
    {
        preferences = sharedPreferences;
        setPreferences(sharedPreferences);
    }

    @Override
    public void onLoaderReset(Loader<SharedPreferences> sharedPreferencesLoader)
    {
        // not used
    }

    /**
     * Private façade that calls to real methods that display
     * the signal info on the screen
     *
     * @param filteredSignals - the filtered signals ready to display
     */
    private void displaySignalInfo(String[] filteredSignals)
    {
        SignalMapWrapper signalMapWrapper = new SignalMapWrapper(filteredSignals, tm);

        if (signalMapWrapper.hasData()) {
            displaySignalInfo(signalMapWrapper);
        }
        else {
            Toast.makeText(this,
                getString(R.string.deviceNotSupported),
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets the preferences for the activity (pretty obvious)
     *
     * @param sharedPreferences - preferences to load
     */
    private void setPreferences(SharedPreferences sharedPreferences)
    {
        String signalMeasure = sharedPreferences.getString(
            getString(R.string.signalFormatKey),
            getString(R.string.relativeReading));

        boolean keepScreenOn = sharedPreferences.getBoolean(
            getString(R.string.keepScreenOnKey),
            getResources().getBoolean(R.bool.keepScreenOnDefault));

        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        enableDebug = sharedPreferences.getBoolean(
            getString(R.string.enableDebugKey),
            getResources().getBoolean(R.bool.enableDebugDefault));

        if (signalMeasure.equals(getString(R.string.dB))) {
            // only show decibel readings
            dbOnly = true;
        }
        else {
            // adjust the signal for realistic readings if not set to strictReadings (3GPP values)
            if (signalMeasure.equals(getString(R.string.relativeReading))) {
                fudgeSignal = true;
            }
            else {
                fudgeSignal = false;
            }
            dbOnly = false;
        }
    }

    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private void setPhoneInfo()
    {
        setTextViewText(R.id.deviceName, String.format("%s %s", Build.MANUFACTURER, Build.MODEL));
        setTextViewText(R.id.deviceModel, String.format("%s/%s (%s) ", Build.PRODUCT, Build.DEVICE, Build.ID));
        setTextViewText(R.id.androidVersion,
            String.format(
                getString(R.string.androidVersion),
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT));

        setTextViewText(R.id.carrierName, tm.getNetworkOperatorName());
        setTextViewText(R.id.buildHost, Build.HOST);
        setNetworkTypeText();
    }

    private void setTextViewText(int txtViewId, CharSequence text)
    {
        ((TextView) findViewById(txtViewId)).setText(text);
    }

    private void setNetworkTypeText()
    {
        setTextViewText(R.id.networkType, SignalInfo.getConnectedNetworkString(tm));
    }

    /**
     * Formats the page footer with in the following format:
     * ©YEAR codingcreation.com | v. x.xx
     */
    private void formatFooter()
    {
        try {
            String appVersion = getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;

            setTextViewText(R.id.copyright,
                String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance().get(Calendar.YEAR),
                    appVersion));

            findViewById(R.id.copyright).setContentDescription(
                String.format(getString(R.string.copyrightDescription),
                    appVersion));
        } catch (PackageManager.NameNotFoundException ignored) {
            Log.wtf(TAG, "Could not display app version number!");
        }
    }

    /**
     * Binds the TextViews to the signal data to show
     * to the user.
     *
     * @param signalMapWrapper - data to display in the view
     */
    private void displaySignalInfo(SignalMapWrapper signalMapWrapper)
    {
        Map<NetworkType, ISignal> networkTypes = signalMapWrapper.getNetworkMap();
        Map<Signal, TextView> signalDataMap = getSignalTextViewMap(sigInfoIds, false);
        String unit = getString(R.string.dBm);

        for (Map.Entry<Signal, TextView> data : signalDataMap.entrySet()) {
            // TODO: maybe use an adapter of some sort instead of this (ListAdapter maybe?)
            TextView currentTextView = data.getValue();
            try {
                ISignal signal = networkTypes[data.getKey().type()];
                String sigValue = signal.getSignalString(data.getKey());

                if (!StringUtils.isNullOrEmpty(sigValue) && !DEFAULT_TXT.equals(sigValue)) {
                    // should be show the percentage along with the dBm?
                    String signalPercent = dbOnly
                        ? ""
                        : String.format("(%s)", signal.getRelativeEfficiency(data.getKey(), fudgeSignal));

                    currentTextView.setText(String.format("%s %s %s",
                        sigValue,
                        unit,
                        signalPercent));
                }
            } catch (Resources.NotFoundException ignored) {
                currentTextView.setText(DEFAULT_TXT);
            }
        }
        setNetworkTypeText(); // update the network connection type
    }

    private void displayDebugInfo(SignalArrayWrapper debugInfo)
    {
        if (enableDebug) {
            View view = findViewById(R.id.debugInfo);

            if (!view.isEnabled()) {
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
                view = findViewById(R.id.debugTbl);
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
            }
            setTextViewText(R.id.debugArray,
                String.format("%s | %s",
                    debugInfo.getRawData(),
                    Arrays.toString(debugInfo.getFilteredArray())));
        }
    }

    /**
     * Loads up the preferences screen in the action bar.
     */
    private void loadPrefsScreen()
    {
        Intent intent = new Intent(this, EditSettings.class);

        // if using Android 3.0+ and only using one preference screen, default load it
        // instead of using the big ass selection header area for preferences.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
            && getResources().getBoolean(R.bool.suppressHeader)) {

            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                SettingsFragment.class.getName());

            Bundle bundle = new Bundle();
            bundle.putString("resource", "main_prefs");
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
        }
        startActivity(intent);
    }
}
