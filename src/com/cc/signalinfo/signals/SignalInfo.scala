package com.cc.signalinfo.signals

import java.util.{ArrayList ⇒ Alist, Collections, EnumMap ⇒ Emap, EnumSet ⇒ Eset, LinkedHashMap ⇒ Lmap, LinkedHashSet ⇒ Lset, List ⇒ Jlist, Map ⇒ Jmap, Set ⇒ Jset}

import android.telephony.TelephonyManager
import android.telephony.TelephonyManager._
import com.cc.signalinfo.config.AppSetup.DEFAULT_TXT
import com.cc.signalinfo.enums.{NetworkType, Signal}
import com.cc.signalinfo.util.StringUtils
import org.jetbrains.annotations.Nullable

/**
 * The parent class for each specific type of signal readings.
 *
 * @author Wes Lanning
 * @version 2013-04-29
 */
object SignalInfo {
  /**
   * Gets the textual name for the type specific type of
   * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
   *
   * Newer supported network types are near the bottom to avoid any issues with shitty old devices.
   *
   * @param tm - instance of telephonyManager
   * @return the given name for the network type the device is using currently for data
   */
  def networkType(tm: TelephonyManager): String = {
    tm.getNetworkType match {
      case NETWORK_TYPE_CDMA ⇒
        "CDMA"
      case NETWORK_TYPE_EDGE ⇒
        "EDGE"
      case NETWORK_TYPE_EVDO_0 ⇒
        "Ev-DO rev. 0"
      case NETWORK_TYPE_EVDO_A ⇒
        "Ev-DO rev. A"
      case NETWORK_TYPE_GPRS ⇒
        "GPRS"
      case NETWORK_TYPE_HSDPA ⇒
        "HSDPA"
      case NETWORK_TYPE_HSUPA ⇒
        "HSUPA"
      case NETWORK_TYPE_HSPA ⇒
        "HSPA"
      case NETWORK_TYPE_1xRTT ⇒
        "1xRTT"
      case NETWORK_TYPE_UMTS ⇒
        "UMTS"
      case NETWORK_TYPE_IDEN ⇒
        "iDen"
      case NETWORK_TYPE_EVDO_B ⇒
        "Ev-DO rev. B"
      case NETWORK_TYPE_LTE ⇒
        "LTE"
      case NETWORK_TYPE_EHRPD ⇒
        "eHRPD"
      case NETWORK_TYPE_HSPAP ⇒
        "HSPA+"
      case _ ⇒
        "Unknown"
    }
  }

  def network(tm: TelephonyManager): NetworkType = {
    tm.getNetworkType match {
      case NETWORK_TYPE_CDMA | NETWORK_TYPE_EDGE | NETWORK_TYPE_EVDO_0 | NETWORK_TYPE_EVDO_A
           | NETWORK_TYPE_EVDO_B | NETWORK_TYPE_EHRPD | NETWORK_TYPE_1xRTT ⇒ NetworkType.CDMA
      case NETWORK_TYPE_GPRS | NETWORK_TYPE_HSDPA | NETWORK_TYPE_HSPA | NETWORK_TYPE_HSPAP
           | NETWORK_TYPE_HSUPA | NETWORK_TYPE_UMTS ⇒ NetworkType.GSM
      case NETWORK_TYPE_LTE ⇒ NetworkType.LTE
      case _ ⇒ NetworkType.UNKNOWN
    }
  }

  /**
   * Converts a signal reading from centibels to decibels
   *
   * @param centibels - the signal reading in cB
   * @return the signal reading in dB
   */
  def cb2db(centibels: String): String = {
    var centibelCopy = centibels

    if (!StringUtils.isNullOrEmpty(centibelCopy) && DEFAULT_TXT != centibelCopy) {
      centibelCopy = String.valueOf(Integer.parseInt(centibelCopy) / 10)
    }
    centibelCopy
  }
}

/**
 * Instantiates a new Signal info.
 *
 * @param networkType - the type of network
 * @param tm - instance of telephonyManager
 * @param pSignals - the signals to add
 */
abstract class SignalInfo(protected val networkType: NetworkType,
                          protected val tm: TelephonyManager,
                          @Nullable protected val pSignals: Jmap[Signal, String])
  extends ISignal {
  protected val signals = if (pSignals == null) {
    new Emap[Signal, String](classOf[Signal])
  }
  else {
    new Emap[Signal, String](pSignals)
  }

  /**
   * Screw Android for using centibels when they should
   * be using decibels for things like SNR.
   *
   * if true, convert all non-decibel readings (centibels) to decibels
   */
  protected var preferDb       = true
  /**
   * The Possible values for the current network type
   */
  protected var possibleValues = Eset.noneOf(classOf[Signal])

  /**
   * Instantiates a new Signal info.
   *
   * @param networkType - the type of network
   * @param tm - instance of telephonyManager
   * @param signals - the signals to add
   * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
   */
  protected def this(networkType: NetworkType,
                     tm: TelephonyManager,
                     signals: Jmap[Signal, String],
                     preferDb: Boolean) {
    this(networkType, tm, signals)
    this.preferDb = preferDb
  }

  /**
   * Instantiates a new Signal info.
   *
   * @param networkType - the type of network
   * @param tm - instance of telephonyManager
   */
  protected def this(networkType: NetworkType, tm: TelephonyManager) {
    this(networkType, tm, null)
  }

  /**
   * Gets signal string given the SignalType
   *
   * @param signalType - the signalType
   * @return the signal string or null if doesn't exist
   */
  def signalString(signalType: Signal): String = {
    signals.get(signalType)
  }

  /**
   * The percent from 0 (worst) 100 (best)
   * of how great the current signal measurement is.
   *
   * May be imprecise due to carrier differences for
   * certain measures (like RSSI), but this is more
   * user friendly for those not interested in what
   * the measures actually mean and their measurement range.
   *
   * @param name - the name of the reading to compute
   * @param fudgeReading - set to true, fudge the reading to make the user feel better while ignoring standards
   * @return the relative efficiency as a percent
   */
  def relativeEfficiency(name: Signal, fudgeReading: Boolean): String = {
    var signalValue: Float =
      if (DEFAULT_TXT == signals.get(name)) {
        -1
      }
      else {
        Math.abs(Integer.parseInt(signals.get(name)))
      }

    if (signalValue == -1) {
      return "" // no value set
    }
    // ex: LTE RSRP = 80db, best = 0, worst = 76, normalized = -44db
    // normalize the reading to align to zero
    signalValue += name.norm

    // 80db - 44db = 36db
    val fudgeValue =
      if (name.best > name.worst) {
        0
      }
      else {
        (name.worst - signalValue) / 100.00f
      }

    val result: Float =
      if (name.best > name.worst) {
        signalValue / name.best + fudgeValue
      }
      else {
        (name.worst - signalValue) / name.worst + fudgeValue
      }

    var percentSignal = Math.round(result * 100)
    percentSignal = if (percentSignal < 0) 0 else Math.abs(percentSignal)
    percentSignal = if (percentSignal > 100) 100 else percentSignal

    s"$percentSignal%"
  }

  /**
   * Gets all the percentages of relative efficiency for the current network instead of just one.
   *
   * @param fudgeReading - set to true, fudge the reading to make the user feel better while ignoring standards
   * @return the % of all readings as a map of name of the reading as the key and the value as the value
   *
   * @see SignalInfo#getRelativeEfficiency(com.cc.signalinfo.enums.Signal, boolean)
   */
  def getRelativeEfficiencyMap(fudgeReading: Boolean): Jmap[String, String] = {
    import scala.collection.JavaConversions.mapAsScalaMap
    val readings = new Lmap[String, String]

    for ((signalKey, signalValue) ← signals) {
      readings(signalKey.name) = relativeEfficiency(signalKey, fudgeReading)
    }
    readings
  }

  /**
   * Gets the signal reading for the given signal type as an integer value
   *
   * @param name the name of the signal reading (RSSI, SNR, etc)
   * @return the signal reading (you better make sure it exists before getting it)
   */
  def get(name: Signal): Int = {
    Integer.parseInt(signals.get(name))
  }

  /**
   * Gets all the signal types and readings for the current
   * network collection.
   *
   * @return the signals
   */
  def getSignals: Jmap[Signal, String] = {
    Collections.unmodifiableMap(signals)
  }

  /**
   * Gets signal type names for the currently stored
   * signal readings.
   *
   * @return the signal names
   */
  def getSignalNames: Jset[Signal] = {
    signals.keySet
  }

  /**
   * Does the current network type (gsm, cdma, etc)
   * contain the given type of signal?
   *
   * @param networkType the type
   * @return true if network type contains this type of signal
   */
  def containsSignalType(networkType: Signal): Boolean = {
    possibleValues contains networkType
  }

  /**
   * Gets the numerical value representing the specific type of
   * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
   *
   * @return the Android API integer that represents the network type
   */
  def getConnectedNetworkValue: Int = {
    tm.getNetworkType
  }

  /**
   * Gets the textual name for the type specific type of
   * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
   *
   * Newer supported network types are near the bottom to avoid any issues with shitty old devices.
   *
   * @return the given name for the network type the device is using currently for data
   */
  def getConnectedNetworkString: String = {
    SignalInfo.networkType(tm)
  }

  /**
   * Get the primary device radio type as a numerical value defined by
   * Android OS.
   *
   * @return the Android API integer that represents the network type
   */
  def getDeviceType: Int = {
    tm.getPhoneType
  }

  /**
   * Get the primary device radio type as a string(GSM, CDMA, SIP, None)
   *
   * @return device type as a string
   */
  def getDeviceTypeString: String = {
    tm.getPhoneType match {
      case PHONE_TYPE_GSM ⇒
        "GSM Device"
      case PHONE_TYPE_CDMA ⇒
        "CDMA Device"
      case PHONE_TYPE_NONE ⇒
        "No Cellular Radio"
      case PHONE_TYPE_SIP ⇒
        "Voice over IP (VoIP)"
      case _ ⇒
        "No Cellular Radio"
    }
  }

  /**
   * Gets the network type for this SignalType instantiation
   *
   * @return the network type
   */
  def getNetworkType: NetworkType = {
    networkType
  }

  def getStringSets: Jlist[Lset[String]] = {
    val signalSet = new Alist[Lset[String]]
    val signalNames = new Lset[String]
    val signalValues = new Lset[String]

    import scala.collection.JavaConversions._

    for (signal <- signals.entrySet) {
      signalNames += signal.getKey.name
      signalValues += signal.getValue
    }
    signalSet += signalNames
    signalSet += signalValues
    signalSet
  }

  /**
   * Add a signal value to the current network type collection.
   *
   * @param type the type (like RSSI, RSRP, SNR, etc)
   * @param value the value (the current reading from the tower for the signal)
   * @return the value of any previous signal value with the
   *         specified type or null if there was no signal already added.
   */
  def addSignalValue(`type`: Signal, value: String): String = {
    var valueCpy = value

    if (decibelsPreferred(`type`)) {
      valueCpy = SignalInfo.cb2db(valueCpy)
    }
    signals.put(`type`, valueCpy)
  }

  /**
   * Number of signal readings contained in the class collection
   *
   * @return # of signal readings
   */
  def size: Int = {
    signals.size
  }

  def decibelsPreferred(`type`: Signal): Boolean = {
    ((`type` == Signal.CDMA_ECIO
      || `type` == Signal.EVDO_ECIO
      || `type` == Signal.LTE_SNR)
      && preferDb)
  }
}

