package com.cc.signalinfo.signals

import android.telephony.TelephonyManager
import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import java.util.{Map ⇒ Jmap, EnumSet ⇒ Eset}
import com.cc.signalinfo.config.AppSetup.DEFAULT_TXT
import com.cc.signalinfo.util.StringUtils.isNullOrEmpty
import com.cc.signalinfo.util.StringUtils.safeEquals
import java.lang.Integer.parseInt

/**
 * Stores all the signal info related to LTE
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 * @param tm - instance of telephonyManager
 * @param pSignals the signals
 */
class LteInfo(tm: TelephonyManager, pSignals: Jmap[Signal, String]) extends SignalInfo(NetworkType.LTE, tm, pSignals)
{
    possibleValues = Eset.range(Signal.LTE_SIG_STRENGTH, Signal.LTE_RSSI)
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
        !isNullOrEmpty(signals.get(Signal.LTE_RSRP))
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
        var valueCopy: String = value

        if (signalType == Signal.LTE_RSRQ && !safeEquals(valueCopy, DEFAULT_TXT)) {
            if (valueCopy.charAt(0) != '-') {
                valueCopy = '-' + valueCopy
            }
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
            && !(DEFAULT_TXT == signals.get(Signal.LTE_RSRP))
            && !(DEFAULT_TXT == signals.get(Signal.LTE_RSRQ)))
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

