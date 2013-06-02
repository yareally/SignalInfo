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

package com.cc.signalinfo.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.cc.signalinfo.BuildConfig;
import com.cc.signalinfo.R;
import com.cc.signalinfo.dialogs.WarningDialogFragment;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.listeners.SignalListener;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.signals.SignalInfo;
import com.cc.signalinfo.util.SignalArrayWrapper;
import com.cc.signalinfo.util.SignalHelpers;
import com.cc.signalinfo.util.SignalMapWrapper;
import com.cc.signalinfo.util.StringUtils;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.*;

import static com.cc.signalinfo.config.AppSetup.DEFAULT_TXT;

/**
 * Not currently used because it's not really necessary (yet)
 * @author Wes Lanning
 * @version 2013-05-10
 */
@SuppressWarnings("ReuseOfLocalVariable")
public class SignalFragment extends SherlockFragment implements View.OnClickListener, SignalListener.UpdateSignal
{
    private final String                    TAG               = getClass().getSimpleName();
    private       FragmentActivity          activity          = null;
    private       SignalListener            listener          = null;
    private       View                      rootView          = null;
    //private       String[]         sigInfoTitles  = null;
    private       TypedArray                sigInfoIds        = null;
    private       EnumMap<Signal, TextView> signalTextViewMap = new EnumMap<>(Signal.class);
    private       TelephonyManager          tm                = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        setRetainInstance(true);
        activity = getActivity();
        rootView = inflater.inflate(R.layout.main, parent, false);
        rootView.findViewById(R.id.additionalInfo).setOnClickListener(this);
        sigInfoIds = getResources().obtainTypedArray(R.array.sigInfoIds);
        // sigInfoTitles = getResources().getStringArray(R.array.sigInfoTitles);

        SignalListener listener = new SignalListener(this);
        tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        setPhoneInfo();
        formatFooter();

        if (!BuildConfig.DEBUG) {
            AdView ad = (AdView) rootView.findViewById(R.id.adView);
            ad.loadAd(new AdRequest());
        }
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

/*    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (rootView != null) {
            outState.putInt("rootView", rootView.getId());
        }

        if (signalTextViewMap != null) {
            outState.putSerializable("signalTextViewMap", signalTextViewMap);
        }
    }*/

    /**
     * Shows additional radio settings contained in the Android OS.
     *
     * @param view - button that shows the settings.
     */
    @Override
    public void onClick(View view)
    {
        if (SignalHelpers.userConsent(activity.getPreferences(Context.MODE_PRIVATE))) {
            try {
                startActivity(SignalHelpers.getAdditionalSettings());
            } catch (SecurityException | ActivityNotFoundException ignored) {
                Toast.makeText(activity,
                    activity.getString(R.string.noAdditionalSettingSupport),
                    Toast.LENGTH_LONG).show();
            }
        }
        else {
            new WarningDialogFragment().show(activity.getSupportFragmentManager(), "Warning");
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
                    TextView currentView = (TextView) rootView.findViewById(id);
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
    public void setData(SignalArrayWrapper signalStrength)
    {
        if (signalStrength == null) {
            return;
        }
        SignalMapWrapper signalMapWrapper = new SignalMapWrapper(signalStrength.getFilteredArray(), tm);

        if (signalMapWrapper.hasData()) {
            displayDebugInfo(signalStrength);
            displaySignalInfo(signalMapWrapper);
        }
        else {
            Toast.makeText(activity,
                getString(R.string.deviceNotSupported),
                Toast.LENGTH_LONG).show();
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
                activity.getString(R.string.androidVersion),
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT));

        setTextViewText(R.id.carrierName, tm.getNetworkOperatorName());
        setTextViewText(R.id.buildHost, Build.HOST);
        setNetworkTypeText();
    }

    private void setTextViewText(int txtViewId, CharSequence text)
    {
        ((TextView) rootView.findViewById(txtViewId)).setText(text);
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
            String appVersion = activity.getPackageManager()
                .getPackageInfo(activity.getPackageName(), 0).versionName;

            setTextViewText(R.id.copyright,
                String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance().get(Calendar.YEAR),
                    appVersion));

            rootView.findViewById(R.id.copyright).setContentDescription(
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
        //  boolean enableSignals = menuItemStates.get(id.enable_strict_readings);
        // boolean relativeSignals = menuItemStates.get(id.enable_relative_readings);

        for (Map.Entry<Signal, TextView> data : signalDataMap.entrySet()) {
            // TODO: maybe use an adapter of some sort instead of this (ListAdapter maybe?)
            TextView currentTextView = data.getValue();
            try {
                ISignal signal = networkTypes.get(data.getKey().type());
                String sigValue = signal.getSignalString(data.getKey());

                if (!StringUtils.isNullOrEmpty(sigValue) && !DEFAULT_TXT.equals(sigValue)) {
  /*                  if (!menuItemStates.get(id.enable_db_readings)) {
                        sigValue = "";
                        unit = "";
                    }*/
                    String signalPercent = true
                        ? String.format("(%s)", signal.getRelativeEfficiency(data.getKey(), true))
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

    private void displayDebugInfo(SignalArrayWrapper debugInfo)
    {
        if (BuildConfig.DEBUG) {
            View view = rootView.findViewById(R.id.debugInfo);

            if (!view.isEnabled()) {
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
                view = rootView.findViewById(R.id.debugTbl);
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
            }
            setTextViewText(R.id.debugArray,
                String.format("%s | %s",
                    debugInfo.getRawData(),
                    Arrays.toString(debugInfo.getFilteredArray())));
        }
    }
}
