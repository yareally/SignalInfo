package com.cc.signalinfo.signals

import android.telephony.TelephonyManager
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import com.cc.signalinfo.util.StringUtils
import java.util.{EnumSet ⇒ Eset, EnumMap ⇒ Emap}

/**
 * Stores all signal info related to 3G and below GSM
 *
 * @param tm - instance of telephonyManager
 * @param pSignals the signals
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
class GsmInfo(tm: TelephonyManager, pSignals: Emap[Signal, String]) extends SignalInfo(NetworkType.GSM, tm, pSignals)
{
    possibleValues = Eset.of(Signal.GSM_SIG_STRENGTH, Signal.GSM_RSSI, Signal.GSM_BIT_ERROR, Signal.GSM_ECIO)

    /**
     * Instantiates a new Gsm info.
     *
     * @param tm - instance of telephonyManager
     * @param pSignals the signals
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    def this(tm: TelephonyManager, pSignals: Emap[Signal, String], preferDb: Boolean) {
        this(tm, pSignals)
        this.preferDb = preferDb
    }

    /**
     * Instantiates a new Gsm info.
     *
     * @param tm - instance of telephonyManager
     */
    def this(tm: TelephonyManager) {
        this(tm, null)
    }

    /**
     * Checks to see if we have an rsrp and rsrq signal. If either
     * is the DEFAULT_TXT set for the rsrp/rsrq or null, then we assume
     * we can't calculate an estimated RSSI signal.
     *
     * @return true if RSSI possible, false if not
     */
    private def hasGsmRssi: Boolean = {
        (!StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH))
            && AppSetup.DEFAULT_TXT != signals.get(Signal.GSM_SIG_STRENGTH))
    }

    /**
     * Computes the GSM RSSI for the device
     *
     * @return the RSSI signal
     */
    private def computeRssi: Int = {
        val gsmSignalStrength = Integer.parseInt(signals.get(Signal.GSM_SIG_STRENGTH))
        -113 + (2 * gsmSignalStrength)
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
        val oldValue: String = super.addSignalValue(signalType, value)

        if (hasGsmRssi) {
            super.addSignalValue(Signal.GSM_RSSI, String.valueOf(computeRssi))
        }
        oldValue
    }

    /**
     * Is the current network type being used on the device?
     * Return of false means there's no signal currently, not that
     * the device cannot receive signals of this type of network.
     *
     * @return true if enabled
     */
    def enabled: Boolean = {
        !StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH))
    }
}

