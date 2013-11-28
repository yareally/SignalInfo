package com.cc.signalinfo.signals

import android.telephony.TelephonyManager
import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import com.cc.signalinfo.util.StringUtils
import java.util.{Map ⇒ Jmap, EnumSet ⇒ Eset}

/**
 * Stores all signal info related to CDMA
 *
 * @param tm - instance of telephonyManager
 * @param pSignals the signals
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
class CdmaInfo(tm: TelephonyManager, pSignals: Jmap[Signal, String])
    extends SignalInfo(NetworkType.CDMA, tm, pSignals)
{

    possibleValues = Eset.range(Signal.CDMA_RSSI, Signal.EVDO_SNR)


    /**
     * Instantiates a new Cdma info.
     *
     * @param tm - instance of telephonyManager
     * @param pSignals the signals
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    def this(tm: TelephonyManager, pSignals: Jmap[Signal, String], preferDb: Boolean) {
        this(tm, pSignals)
        this.preferDb = preferDb
    }

    /**
     * Instantiates a new Cdma info.
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
        (!StringUtils.isNullOrEmpty(signals.get(Signal.CDMA_RSSI))
            || !StringUtils.isNullOrEmpty(signals.get(Signal.EVDO_RSSI)))
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

        if (decibelsPreferred(signalType)) {
            valueCopy = SignalInfo.cb2db(valueCopy)
        }
        super.addSignalValue(signalType, valueCopy)
    }

    private def decibelsPreferred(signalType: Signal): Boolean = {
        (signalType == Signal.CDMA_ECIO || signalType == Signal.EVDO_ECIO) && preferDb
    }
}

