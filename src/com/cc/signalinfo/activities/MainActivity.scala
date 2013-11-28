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
package com.cc.signalinfo.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.support.v4.content.Loader
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.cc.signalinfo.R
import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import com.cc.signalinfo.listeners.SignalListener
import com.cc.signalinfo.signals.ISignal
import com.cc.signalinfo.signals.SignalInfo
import com.cc.signalinfo.util._
import com.commonsware.cwac.loaderex.acl.SharedPreferencesLoader
import java.util.{Map ⇒ Jmap, EnumMap ⇒ Emap, Collections}
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.view.View.OnClickListener
import com.cc.signalinfo.config.AppSetup.DEFAULT_TXT
import com.cc.signalinfo.dialogs.WarningDialog


// ↑ Because the over verbosity on the constants will probably give me brain damage...
/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
class MainActivity
    extends BaseActivity
    with OnClickListener
    with SignalListener.UpdateSignal
    with LoaderCallbacks[SharedPreferences]
{
    private var dbOnly           : Boolean               = false
    private var enableDebug      : Boolean               = false
    private var filteredSignals  : Array[String]         = null
    private var fudgeSignal      : Boolean               = true
    private val listener         : SignalListener        = null
    private var preferences      : SharedPreferences     = null
    private var sigInfoIds       : TypedArray            = null
    private val signalTextViewMap                        = new Emap[Signal, TextView](classOf[Signal])
    private var tm: TelephonyManager = null

    /**
     * Initialize the app.
     *
     * @param savedInstanceState - umm... the saved instance state
     */
    override def onCreate(savedInstanceState: Bundle) {
        onCreate(R.layout.radio_signal_fragment, savedInstanceState)

        val listener: SignalListener = new SignalListener(this)
        sigInfoIds = getResources.obtainTypedArray(R.array.sigInfoIds)

        tm = getSystemService(Context.TELEPHONY_SERVICE).asInstanceOf[TelephonyManager]
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)

        getSupportLoaderManager.initLoader(0, null, this)
        findViewById(R.id.additionalInfo).setOnClickListener(this)
        setPhoneInfo()
    }

    /**
     * Gets the TextViews that map to the signal info data in the code for binding.
     *
     * @param sigInfoIds - the array containing the IDs to the TextView resources
     * @param refreshMap - should we recreate the map or reuse it? (in case we some reason added some, somehow)
     * @return map of the Signal data enumeration types (keys) and corresponding TextViews (values)
     */
    def getSignalTextViewMap(sigInfoIds: TypedArray, refreshMap: Boolean): Jmap[Signal, TextView] = {
        if (signalTextViewMap.isEmpty || refreshMap) {
            val values: Array[Signal] = Signal.values

            for (i ← 0 until sigInfoIds.length) {
                val id: Int = sigInfoIds.getResourceId(i, -1)
                if (id != -1) {
                    val currentView: TextView = findViewById(id).asInstanceOf[TextView]
                    signalTextViewMap.put(values(i), currentView)
                }
            }
        }
        return Collections.unmodifiableMap(signalTextViewMap)
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    override def setData(signalStrength: SignalArrayWrapper) {
        if (signalStrength == null) {
            return
        }
        filteredSignals = signalStrength.getFilteredArray
        displayDebugInfo(signalStrength)
        displaySignalInfo(filteredSignals)
    }

    override def onResume() {
        super.onResume()
        if (preferences != null) {
            setPreferences(preferences)

            if (filteredSignals != null) {
                displaySignalInfo(filteredSignals)
            }
        }
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    /**
     * Shows additional radio settings contained in the Android OS.
     *
     * @param view - button that shows the settings.
     */
    def onClick(view: View) {
        if (AppHelpers.userConsent(getPreferences(Context.MODE_PRIVATE))) {
            try {
                startActivity(AppHelpers.getAdditionalSettings)
            }
            catch {
                case ignored: Any =>
                    Toast.makeText(this, getString(R.string.noAdditionalSettingSupport), Toast.LENGTH_LONG).show()
            }
        }
        else {
            new WarningDialog().show(getSupportFragmentManager, "Warning")
        }
    }

    override def onPause() {
        super.onPause()
        tm.listen(listener, PhoneStateListener.LISTEN_NONE)
    }

    /**
     * Create a new shared preferences loader when there isn't one or
     * one is no longer instantiated
     *
     * @param i - the id for the loader we want (typically 0, but not always)
     * @param bundle - any extra stuff to fetch (probably not used)
     * @return the SharedPreferencesLoader
     */
    def onCreateLoader(i: Int, bundle: Bundle): Loader[SharedPreferences] = {
        return new SharedPreferencesLoader(this)
    }

    /**
     * After the preferences have been loaded, do the stuff here
     *
     * @param sharedPreferencesLoader - loader for the preferences
     * @param sharedPreferences - all the previously saved user preferences and such
     */
    def onLoadFinished(sharedPreferencesLoader: Loader[SharedPreferences], sharedPreferences: SharedPreferences) {
        preferences = sharedPreferences
        setPreferences(sharedPreferences)
    }

    def onLoaderReset(sharedPreferencesLoader: Loader[SharedPreferences]) {
        // not used
    }

    /**
     * Private façade that calls to real methods that display
     * the signal info on the screen
     *
     * @param filteredSignals - the filtered signals ready to display
     */
    private def displaySignalInfo(filteredSignals: Array[String]) {
        val signalMapWrapper: SignalMapWrapper = new SignalMapWrapper(filteredSignals, tm)

        if (signalMapWrapper.hasData) {
            displaySignalInfo(signalMapWrapper)
        }
        else {
            Toast.makeText(this, getString(R.string.deviceNotSupported), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sets the preferences for the activity (pretty obvious)
     *
     * @param sharedPreferences - preferences to load
     */
    private def setPreferences(sharedPreferences: SharedPreferences) {
        val signalMeasure: String = sharedPreferences.getString(
            getString(R.string.signalFormatKey),
            getString(R.string.relativeReading))

        val keepScreenOn: Boolean = sharedPreferences.getBoolean(
            getString(R.string.keepScreenOnKey),
            getResources.getBoolean(R.bool.keepScreenOnDefault))

        if (keepScreenOn) {
            getWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        enableDebug = sharedPreferences.getBoolean(
            getString(R.string.enableDebugKey),
            getResources.getBoolean(R.bool.enableDebugDefault))

        if (signalMeasure == getString(R.string.dB)) {
            dbOnly = true
        }
        else {
            fudgeSignal = signalMeasure == getString(R.string.relativeReading)
            dbOnly = false
        }
    }

    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private def setPhoneInfo() {
        setTextViewText(R.id.deviceName, s"${Build.MANUFACTURER } ${Build.MODEL }")
        setTextViewText(R.id.deviceModel, s"${Build.PRODUCT }/${Build.DEVICE } (${Build.ID }) ")
        setTextViewText(R.id.androidVersion,
            String.format(getString(R.string.androidVersion),
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT.asInstanceOf[java.lang.Integer]))

        setTextViewText(R.id.carrierName, tm.getNetworkOperatorName)
        setTextViewText(R.id.buildHost, Build.HOST)
        setNetworkTypeText()
    }

    private def setNetworkTypeText() {
        setTextViewText(R.id.networkType, SignalInfo.getConnectedNetworkString(tm))
    }

    /**
     * Binds the TextViews to the signal data to show
     * to the user.
     *
     * @param signalMapWrapper - data to display in the view
     */
    private def displaySignalInfo(signalMapWrapper: SignalMapWrapper) {
        import scala.collection.JavaConversions.asScalaSet

        val networkTypes: Jmap[NetworkType, ISignal] = signalMapWrapper.getNetworkMap
        val signalDataMap: Jmap[Signal, TextView] = getSignalTextViewMap(sigInfoIds, refreshMap = false)
        val unit: String = getString(R.string.dBm)

        for (data <- signalDataMap.entrySet) {
            val currentTextView: TextView = data.getValue
            try {
                val signal: ISignal = networkTypes.get(data.getKey.`type`)
                val sigValue: String = signal.getSignalString(data.getKey)
                if (!StringUtils.isNullOrEmpty(sigValue) && !(DEFAULT_TXT == sigValue)) {
                    // should be show the percentage along with the dBm?
                    val signalPercent: String =
                        if (dbOnly) ""
                        else s"(${signal.getRelativeEfficiency(data.getKey, fudgeSignal) })"
                    currentTextView.setText(s"$sigValue $unit $signalPercent")
                }
            }
            catch {
                case ignored: Resources.NotFoundException =>
                    currentTextView.setText(DEFAULT_TXT)
            }
        }
        setNetworkTypeText()
    }

    /**
     * For my own usage and if a user wants to see it or give me feedback.
     *
     * @param debugInfo - the signal data to dump
     */
    private def displayDebugInfo(debugInfo: SignalArrayWrapper) {
        if (enableDebug) {
            var view: View = findViewById(R.id.debugInfo)

            if (!view.isEnabled) {
                view.setEnabled(true)
                view.setVisibility(View.VISIBLE)
                view = findViewById(R.id.debugArray)
                view.setEnabled(true)
                view.setVisibility(View.VISIBLE)
            }
            val debugMapRelative: Jmap[String, String] =
                new SignalMapWrapper(debugInfo.getFilteredArray, tm).getPercentSignalMap(adjustReadings = true)
            val debugMapStrict: Jmap[String, String] =
                new SignalMapWrapper(debugInfo.getFilteredArray, tm).getPercentSignalMap(adjustReadings = false)

            setTextViewText(R.id.debugArray,
                s"${debugInfo.getRawData }" +
                    s"\n\n ${debugInfo.getFilteredArray.mkString(",") }" +
                    s"\n\n ${debugMapRelative.toString }" +
                    s"\n\n ${debugMapStrict.toString }")
        }
    }
}

