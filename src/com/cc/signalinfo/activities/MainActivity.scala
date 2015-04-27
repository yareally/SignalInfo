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

import java.util.{Collections, EnumMap ⇒ Emap, Map ⇒ Jmap}

import android.content.res.{Resources, TypedArray}
import android.content.{Context, SharedPreferences}
import android.os.{Build, Bundle}
import android.telephony.{PhoneStateListener, SignalStrength, TelephonyManager}
import android.view.{View, WindowManager}
import android.widget.{TextView, Toast}
import com.cc.signalinfo.R
import com.cc.signalinfo.activities.MainActivity._
import com.cc.signalinfo.config.AppSetup.DEFAULT_TXT
import com.cc.signalinfo.dialogs.WarningDialog
import com.cc.signalinfo.enums.{NetworkType, Signal}
import com.cc.signalinfo.signals.{ISignal, SignalInfo}
import com.cc.signalinfo.util.PimpMyAndroid._
import com.cc.signalinfo.util._
import com.commonsware.cwac.loaderex.acl.SharedPreferencesLoader
import scala.concurrent.{Await}
import scala.concurrent.duration._

/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
class MainActivity extends BaseActivity {
  private lazy val signalTextViewMap                   = new Emap[Signal, TextView](classOf[Signal])
  private lazy val signalStr      : PhoneStateListener = onSignalChanged((signalStrength: SignalStrength) ⇒ {
    if (signalStrength != null) {
      lazy val signalWrapper = new SignalArrayWrapper(signalStrength.toString)
      filteredSignals = signalWrapper.filterSignals(signalStrength.toString)
      displayDebugInfo(signalWrapper)
      displaySignalInfo(filteredSignals)
    }
  })
  private      var dbOnly         : Boolean            = false
  private      var enableDebug    : Boolean            = false
  private      var filteredSignals: Array[String]      = null
  private      var fudgeSignal    : Boolean            = true
  private      var preferences    : SharedPreferences  = null
  private      var sigInfoIds     : TypedArray         = null
  private      var tm             : TelephonyManager   = null

  /**
   * Initialize the app.
   *
   * @param savedInstanceState - umm... the saved instance state
   */
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(R.layout.radio_signal_fragment, savedInstanceState)
    tm = this.getSysService[TelephonyManager](Context.TELEPHONY_SERVICE)
    tm.listen(signalStr, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    sigInfoIds = getResources.obtainTypedArray(R.array.sigInfoIds)

    val loader = this.initLoader[SharedPreferences](
      (loaderId, bundle) ⇒ new SharedPreferencesLoader(this),
      (loader) ⇒ {},
      (loader, sharedPreferences) ⇒ {
        preferences = sharedPreferences
        setPreferences(sharedPreferences)
      })

    getSupportLoaderManager.initLoader(0, null, loader)
    initClickEvents()
    setPhoneInfo()
  }

  def initClickEvents() {
    // for when the additional area setting button is pressed
    findViewById(R.id.additionalInfo).click((view: View) ⇒ {
      if (AppHelpers.userConsent(getPreferences(Context.MODE_PRIVATE))) {
        try {
          startActivity(AppHelpers.getAdditionalSettings)
        }
        catch {
          case ignored: Any ⇒
            try {
              val result = Await.result(TerminalCommands.launchActivity("com.android.settings", "TestingSettings"), 10.seconds)

              if (result != 0) Toast.makeText(this,
                getString(R.string.noAdditionalSettingSupport),
                Toast.LENGTH_LONG).show()
            } catch {
              case ignored: Exception ⇒
                Toast.makeText(this,
                  getString(R.string.noAdditionalSettingSupport),
                  Toast.LENGTH_LONG).show()
            }
        }
      }
      else {
        new WarningDialog().show(getSupportFragmentManager, "Warning")
      }
    })
  }

  /**
   * Set the phone model, OS version, carrier name on the screen
   */
  private def setPhoneInfo() {
    setTextViewText(R.id.deviceName, s"${Build.MANUFACTURER} ${Build.MODEL}")
    setTextViewText(R.id.deviceModel, s"${Build.PRODUCT}/${Build.DEVICE} (${Build.ID}) ")
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
   * Sets the preferences for the activity (pretty obvious)
   *
   * @param sharedPreferences - preferences to load
   */
  private def setPreferences(sharedPreferences: SharedPreferences) {
    val relativeReadings = getString(R.string.relativeReading)

    val signalMeasure = sharedPreferences.getString(getString(R.string.signalFormatKey), relativeReadings)

    val keepScreenOn = sharedPreferences.getBoolean(
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
      fudgeSignal = signalMeasure == relativeReadings
      dbOnly = false
    }
  }

  override def onResume() {
    super.onResume()
    if (preferences != null) {
      setPreferences(preferences)

      if (filteredSignals != null) {
        displaySignalInfo(filteredSignals)
      }
    }
    tm.listen(signalStr, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
  }

  /**
   * Private façade that calls to real methods that display
   * the signal info on the screen
   *
   * @param filteredSignals - the filtered signals ready to display
   */
  private def displaySignalInfo(filteredSignals: Array[String]) {
    val signalMapWrapper = new SignalMapWrapper(filteredSignals, tm)

    if (signalMapWrapper.hasData) {
      displaySignalInfo(signalMapWrapper)
    }
    else {
      Toast.makeText(this, getString(R.string.deviceNotSupported), Toast.LENGTH_LONG).show()
    }
  }

  /**
   * Binds the TextViews to the signal data to show
   * to the user.
   *
   * @param signalMapWrapper - data to display in the view
   */
  private def displaySignalInfo(signalMapWrapper: SignalMapWrapper) {
    import scala.collection.JavaConversions.mapAsScalaMap
    // intellij may lie and say the above is not needed, but it is

    val networkTypes: Jmap[NetworkType, ISignal] = signalMapWrapper.getNetworkMap
    val signalDataMap: Jmap[Signal, TextView] = getSignalTextViewMap(sigInfoIds, refreshMap = false)
    val unit = getString(R.string.dBm)

    for ((k: Signal, v: TextView) ← signalDataMap) {
      try {
        val signal: ISignal = networkTypes.get(k.`type`)
        val sigValue = signal.getSignalString(k)

        if (!StringUtils.isNullOrEmpty(sigValue) && DEFAULT_TXT != sigValue) {
          // should we show the percentage along with the dBm?
          val signalPercent: String = if (dbOnly) "" else s"(${signal.getRelativeEfficiency(k, fudgeSignal)})"
          v.setText(s"$sigValue $unit $signalPercent")
        }
      }
      catch {
        case ignored: Resources.NotFoundException ⇒
          v.setText(DEFAULT_TXT)
      }
    }
    setNetworkTypeText()
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
          val currentView: TextView = this.findView[TextView](id)
          signalTextViewMap.put(values(i), currentView)
        }
      }
    }
    Collections.unmodifiableMap(signalTextViewMap)
  }

  override def onPause() {
    super.onPause()
    tm.listen(signalStr, PhoneStateListener.LISTEN_NONE)
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
        s"${debugInfo.getRawData}" +
          s"\n\n ${debugInfo.getFilteredArray.mkString(",")}" +
          s"\n\n ${debugMapRelative.toString}" +
          s"\n\n ${debugMapStrict.toString}")
    }
  }
}

object MainActivity {
  def onSignalChanged(callback: (SignalStrength) ⇒ Unit): PhoneStateListener = new PhoneStateListener {
    override def onSignalStrengthsChanged(signalStrength: SignalStrength) = callback(signalStrength)
  }
}