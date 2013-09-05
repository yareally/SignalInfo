package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

import static com.cc.signalinfo.config.AppSetup.*;

/**
 * Stores all the signal info related to LTE
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class LteInfo extends SignalInfo
{
    /**
     * Instantiates a new Lte info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     */
    public LteInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.LTE, tm, signals);
        possibleValues = EnumSet.range(Signal.LTE_SIG_STRENGTH, Signal.LTE_RSSI);
    }

    /**
     * Instantiates a new Lte info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    public LteInfo(TelephonyManager tm, Map<Signal, String> signals, boolean preferDb)
    {
        super(NetworkType.LTE, tm, signals, preferDb);
        possibleValues = EnumSet.range(Signal.LTE_SIG_STRENGTH, Signal.LTE_RSSI);
    }

    /**
     * Instantiates a new Lte info.
     *
     * @param tm - instance of telephonyManager
     */
    public LteInfo(TelephonyManager tm)
    {
        this(tm, null);
    }

    /**
     * Checks to see if we have an rsrp and rsrq signal. If either
     * is the DEFAULT_TXT set for the rsrp/rsrq or null, then we assume
     * we can't calculate an estimated RSSI signal.
     *
     * @return true if RSSI possible, false if not
     */
    private boolean hasLteRssi()
    {
        return !StringUtils.isNullOrEmpty(signals[Signal.LTE_RSRP])
            && !StringUtils.isNullOrEmpty(signals[Signal.LTE_RSRQ])
            && !DEFAULT_TXT.equals(signals[Signal.LTE_RSRP])
            && !DEFAULT_TXT.equals(signals[Signal.LTE_RSRQ]);
    }

    /**
     * Computes the LTE RSSI by what is most likely the default number of
     * channels on the LTE device (at least for Verizon).
     *
     * @return the RSSI signal
     */
    private int computeRssi()
    {
        return -(-17 - Integer.parseInt(signals[Signal.LTE_RSRP]) - Integer.parseInt(signals[Signal.LTE_RSRQ]));
    }

    /**
     * Is the current network type being used on the device?
     * Return of false means there's no signal currently, not that
     * the device cannot receive signals of this type of network.
     *
     * @return true if enabled
     */
    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals[Signal.LTE_RSRP]);
    }

    /**
     * Add a signal value to the current network type collection.
     *
     * @param type the type (like RSSI, RSRP, SNR, etc)
     * @param value the value (the current reading from the tower for the signal)
     * @return the value of any previous signal value with the
     *         specified type or null if there was no signal already added.
     */
    @Override
    public String addSignalValue(Signal type, String value)
    {
        if (type == Signal.LTE_RSRQ && !StringUtils.safeEquals(value, DEFAULT_TXT)) {
            if (value.charAt(0) != '-') {
                // RSRQ should always be negative, fuck you Qualcomm chipsets for typically ignoring this.
                value = '-' + value;
            }
        }
        else if (type == Signal.LTE_SNR && preferDb) {
            value = cb2db(value);
        }
        String oldValue = super.addSignalValue(type, value);

        // if we can now add RSSI, do. Have to manually calculate it though
        if (hasLteRssi()) {
            super.addSignalValue(Signal.LTE_RSSI, String.valueOf(computeRssi()));
        }
        return oldValue;
    }
}
