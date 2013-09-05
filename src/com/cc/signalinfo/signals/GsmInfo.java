package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * Stores all signal info related to 3G and below GSM
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class GsmInfo extends SignalInfo
{
    /**
     * Instantiates a new Gsm info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     */
    public GsmInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.GSM, tm, signals);
        possibleValues =
            EnumSet.of(Signal.GSM_SIG_STRENGTH, Signal.GSM_RSSI, Signal.GSM_ASU, Signal.GSM_BIT_ERROR);
    }

    /**
     * Instantiates a new Gsm info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    public GsmInfo(TelephonyManager tm, Map<Signal, String> signals, boolean preferDb)
    {
        super(NetworkType.GSM, tm, signals, preferDb);
        possibleValues =
            EnumSet.of(Signal.GSM_SIG_STRENGTH, Signal.GSM_RSSI, Signal.GSM_ASU, Signal.GSM_BIT_ERROR);
    }

    /**
     * Instantiates a new Gsm info.
     *
     * @param tm - instance of telephonyManager
     */
    public GsmInfo(TelephonyManager tm)
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
    private boolean hasGsmRssi()
    {
        return !StringUtils.isNullOrEmpty(signals[Signal.GSM_SIG_STRENGTH])
            && !AppSetup.DEFAULT_TXT.equals(signals[Signal.GSM_SIG_STRENGTH]);
    }

    /**
     * Computes the GSM RSSI for the device
     *
     * @return the RSSI signal
     */
    private int computeRssi()
    {
        int gsmSignalStrength = Integer.parseInt(signals[Signal.GSM_SIG_STRENGTH]);
        return -113 + (2 * gsmSignalStrength);
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
        String oldValue = super.addSignalValue(type, value);

        // if we can now add RSSI, do. Have to manually calculate it though
        if (hasGsmRssi()) {
            super.addSignalValue(Signal.GSM_RSSI, String.valueOf(computeRssi()));
        }
        return oldValue;
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
        return !StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH));
    }
}
