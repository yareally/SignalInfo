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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cc.signalinfo.BuildConfig;
import com.cc.signalinfo.R;
import com.cc.signalinfo.R.id;
import com.cc.signalinfo.R.layout;
import com.cc.signalinfo.dialogs.WarningDialogFragment;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.fragments.SettingsFragment;
import com.cc.signalinfo.libs.SignalData;
import com.cc.signalinfo.listeners.ActivityListener;
import com.cc.signalinfo.listeners.SignalListener;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.signals.SignalInfo;
import com.cc.signalinfo.util.SettingsHelpers;
import com.cc.signalinfo.util.SignalHelpers;
import com.cc.signalinfo.util.StringUtils;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.*;
import java.util.prefs.Preferences;

import static com.cc.signalinfo.config.AppSetup.*;
// ↑ Because the over verbosity on the constants will probably give me brain damage...

/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
@SuppressWarnings({"RedundantFieldInitialization", "ReuseOfLocalVariable", "LawOfDemeter"})
public class MainActivity extends SherlockFragmentActivity implements View.OnClickListener, ActivityListener
{
    private final String                TAG               = getClass().getSimpleName();
    private       ActionBar             actionBar         = null;
    private       SignalListener        listener          = null;
    private       Map<Integer, Boolean> menuItemStates    = new HashMap<>(8);
    //private       String[]         sigInfoTitles  = null;
    private       TypedArray            sigInfoIds        = null;
    private       Map<Signal, TextView> signalTextViewMap = new EnumMap<>(Signal.class);
    private       TelephonyManager      tm                = null;

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

        menuItemStates.put(id.enable_db_readings, true);
        menuItemStates.put(id.enable_relative_readings, true);
        menuItemStates.put(id.enable_strict_readings, true);
        actionBar = getSupportActionBar();

        setContentView(layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        formatFooter();

        listener = new SignalListener(this);
        // sigInfoTitles = getResources().getStringArray(R.array.sigInfoTitles);
        sigInfoIds = getResources().obtainTypedArray(R.array.sigInfoIds);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        findViewById(id.additionalInfo).setOnClickListener(this);
        setPhoneInfo();

        if (!BuildConfig.DEBUG) {
            AdView ad = (AdView) findViewById(id.adView);
            ad.loadAd(new AdRequest());
        }
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
/*        String menuItemState = getMenuItemPreference(id.enable_db_readings);

        initMenuItem(menu, id.preferences, menuItemState);
        menuItemState = getMenuItemPreference(id.enable_strict_readings);

        initMenuItem(menu, id.enable_strict_readings, menuItemState);
        menuItemState = getMenuItemPreference(id.enable_relative_readings);

        initMenuItem(menu, id.enable_relative_readings, menuItemState);*/
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
            case id.preferences:
                startActivity(new Intent(this, Preferences.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            for (int i = 0; i <= sigInfoIds.length(); ++i) {
                int id = sigInfoIds.getResourceId(i, -1);

                if (id != -1) {
                    TextView currentView = (TextView) findViewById(id);
                    signalTextViewMap.put(values[i], currentView);
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
    public void setData(SignalStrength signalStrength)
    {
        if (signalStrength == null) {
            return;
        }
        SignalData signalData = new SignalData(signalStrength, tm);
        //SettingsHelpers.getSharedPreferences(this).edit().remove(OLD_FUCKING_DEVICE).commit();

        if (SettingsHelpers.getPreference(this, OLD_FUCKING_DEVICE, -1) == -1) {
            // set if this is some old device or not for sanity purposes later
            SettingsHelpers.addSharedPreference(this, OLD_FUCKING_DEVICE, signalData.legacyDevice());
        }

        if (signalData.hasData()) {
            displayDebugInfo(signalStrength.toString());
            displaySignalInfo(signalData);
        }
        else {
            Toast.makeText(this, getString(R.string.deviceNotSupported), Toast.LENGTH_LONG).show();
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

        for (Map.Entry<Integer, Boolean> menuItem : menuItemStates.entrySet()) {
            saveMenuItem(menuItem.getKey(), menuItem.getValue());
        }
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

    private void initMenuItem(Menu menu, int itemId, String menuItemState)
    {
        if (menuItemState.isEmpty()) {
            menuItemStates.put(itemId, menu.getItem(itemId).isEnabled());
        }
        else {
            menuItemStates.put(itemId, Boolean.valueOf(menuItemState));
            menu.getItem(itemId).setChecked(Boolean.valueOf(menuItemState));
        }
    }

    private String getMenuItemPreference(int menuItemId)
    {
        return SettingsHelpers.getPreference(this, String.valueOf(menuItemId), "");
    }

    private void saveMenuItem(int itemId, boolean state)
    {
        SettingsHelpers.addSharedPreference(this,
            String.valueOf(itemId),
            Boolean.toString(state));
    }

    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private void setPhoneInfo()
    {
        setTextViewText(id.deviceName, String.format("%s %s", Build.MANUFACTURER, Build.MODEL));
        setTextViewText(id.deviceModel, String.format("%s/%s (%s) ", Build.PRODUCT, Build.DEVICE, Build.ID));
        setTextViewText(id.androidVersion, String.format("%s (API version %d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));
        setTextViewText(id.carrierName, tm.getNetworkOperatorName());
        setTextViewText(id.buildHost, Build.HOST);
        setNetworkTypeText();
    }

    private void setNetworkTypeText()
    {
        setTextViewText(id.networkType, SignalInfo.getConnectedNetworkString(tm));
    }

    private void setTextViewText(int txtViewId, CharSequence text)
    {
        ((TextView) findViewById(txtViewId)).setText(text);
    }

    /**
     * Formats the page footer with in the following format:
     * ©YEAR codingcreation.com | v. x.xx
     */
    private void formatFooter()
    {
        try {
            String appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

            setTextViewText(id.copyright,
                String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance().get(Calendar.YEAR),
                    appVersion));

            findViewById(id.copyright).setContentDescription(
                String.format(
                    getString(R.string.copyrightDescription),
                    appVersion));
        } catch (PackageManager.NameNotFoundException ignored) {
            Log.wtf(TAG, "Could not display app version number!");
        }
    }

    /**
     * Binds the TextViews to the signal data to show
     * to the user.
     *
     * @param signalData - data to display in the view
     */
    private void displaySignalInfo(SignalData signalData)
    {
        Map<NetworkType, ISignal> networkTypes = signalData.getNetworkMap();
        Map<Signal, TextView> signalDataMap = getSignalTextViewMap(sigInfoIds, false);
        String unit = getString(R.string.dBm);
        boolean enableSignals = menuItemStates.get(id.enable_strict_readings);
        boolean relativeSignals = menuItemStates.get(id.enable_relative_readings);

        for (Map.Entry<Signal, TextView> data : signalDataMap.entrySet()) {
            // TODO: maybe use an adapter of some sort instead of this (ListAdapter maybe?)
            TextView currentTextView = data.getValue();
            try {
                ISignal signal = networkTypes.get(data.getKey().type());
                String sigValue = signal.getSignalString(data.getKey());

                if (!StringUtils.isNullOrEmpty(sigValue) && !DEFAULT_TXT.equals(sigValue)) {
                    if (!menuItemStates.get(id.enable_db_readings)) {
                        sigValue = "";
                        unit = "";
                    }
                    String signalPercent = enableSignals
                        ? String.format("(%s)", signal.getRelativeEfficiency(data.getKey(), relativeSignals))
                        : "";

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

    private void displayDebugInfo(CharSequence debugInfo)
    {
        if (BuildConfig.DEBUG) {
            View view = findViewById(id.debugInfo);

            if (!view.isEnabled()) {
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
                view = findViewById(id.debugTbl);
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
            }
            setTextViewText(id.debugArray, debugInfo);
        }
    }

    /**
     * Loads up the preferences screen in the action bar.
     */
    private void loadPrefsScreen()
    {
        Intent intent = new Intent(this, EditPreferences.class);

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
