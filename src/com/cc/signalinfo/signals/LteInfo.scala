package com.cc.signalinfo.signals

import java.lang.Integer.parseInt
import java.util.{EnumSet ⇒ Eset, Map ⇒ Jmap}

import android.telephony.TelephonyManager
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.config.AppSetup.DEFAULT_TXT
import com.cc.signalinfo.enums.{NetworkType, Signal}
import com.cc.signalinfo.util.StringUtils.{isNullOrEmpty, safeEquals}

/**
 * Stores all the signal info related to LTE
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 * @param tm - instance of telephonyManager
 * @param pSignals the signals
 */
class LteInfo(tm: TelephonyManager, pSignals: Jmap[Signal, String]) extends SignalInfo(NetworkType.LTE, tm, pSignals) {
  possibleValues = Eset.range(Signal.LTE_SIG_STRENGTH, Signal.LTE_RSSI)
  // used to calculate the RSSI for most LTE networks,
  // but not all (though it's close enough without a good way to determine the band of LTE a device is using)
  private final val RSSI_CONSTANT = 17

  /**
   * Instantiates a new Lte info.
   *
   * @param tm - instance of telephonyManager
   * @param pSignals the signals to add
   * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
   */
  def this(tm: TelephonyManager, pSignals: Jmap[Signal, String], preferDb: Boolean) {
    this(tm, pSignals)
    this.preferDb = preferDb
  }

  /**
   * Instantiates a new Lte info.
   *
   * @param tm - instance of telephonyManager
   */
  def this(tm: TelephonyManager) {
    this(tm, null)
  }

  /**
   * Is the current network type being used on the device?
   * Return of false means there's no signal currently, not that
   * the device cannot receive signals of this type of network.
   *
   * @return true if enabled
   */
  def enabled: Boolean = {
    SignalInfo.network(tm).equals(NetworkType.LTE)
  }

  /**
   * Add a signal value to the current network type collection.
   *
   * @param signalType the type (like RSSI, RSRP, SNR, etc)
   * @param value the value (the current reading from the tower for the signal)
   * @return the value of any previous signal value with the
   *         specified type or null if there was no signal already added.
   */
  override def addSignalValue(signalType: Signal, value: String): String = {
    var valueCopy = if (enabled) value else AppSetup.DEFAULT_TXT

    if (signalType == Signal.LTE_RSRQ && !safeEquals(valueCopy, DEFAULT_TXT) && valueCopy.charAt(0) != '-') {
      valueCopy = '-' + valueCopy
    }
    val oldValue: String = super.addSignalValue(signalType, valueCopy)

    if (hasLteRssi) {
      super.addSignalValue(Signal.LTE_RSSI, String.valueOf(computeRssi))
    }
    oldValue
  }

  /**
   * Checks to see if we have an rsrp and rsrq signal. If either
   * is the DEFAULT_TXT set for the rsrp/rsrq or null, then we assume
   * we can't calculate an estimated RSSI signal.
   *
   * @return true if RSSI possible, false if not
   */
  private def hasLteRssi: Boolean = {
    (!isNullOrEmpty(signals.get(Signal.LTE_RSRP))
      && !isNullOrEmpty(signals.get(Signal.LTE_RSRQ))
      && DEFAULT_TXT != signals.get(Signal.LTE_RSRP)
      && DEFAULT_TXT != signals.get(Signal.LTE_RSRQ))
  }

  /**
   * Computes the LTE RSSI by what is most likely the default number of
   * channels on the LTE device (at least for Verizon).
   *
   * @return the RSSI signal
   */
  private def computeRssi: Int = {
    // 17 + (-108) - (-8)
    RSSI_CONSTANT + parseInt(signals.get(Signal.LTE_RSRP)) - parseInt(signals.get(Signal.LTE_RSRQ))
  }
}

