package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * Stores all signal info related to CDMA
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class CdmaInfo extends SignalInfo
{
    /**
     * Instantiates a new Cdma info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     */
    public CdmaInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.CDMA, tm, signals);
        possibleValues = EnumSet.range(Signal.CDMA_RSSI, Signal.EVDO_SNR);
    }

    /**
     * Instantiates a new Cdma info.
     *
     * @param tm - instance of telephonyManager
     * @param signals the signals
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    public CdmaInfo(TelephonyManager tm, Map<Signal, String> signals, boolean preferDb)
    {
        super(NetworkType.CDMA, tm, signals, preferDb);
        possibleValues = EnumSet.range(Signal.CDMA_RSSI, Signal.EVDO_SNR);
    }

    /**
     * Instantiates a new Cdma info.
     *
     * @param tm - instance of telephonyManager
     */
    public CdmaInfo(TelephonyManager tm)
    {
        this(tm, null);
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
        return !StringUtils.isNullOrEmpty(signals[Signal.CDMA_RSSI])
            || !StringUtils.isNullOrEmpty(signals[Signal.EVDO_RSSI]);
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
        if (decibelsPreferred(type)) {
            value = cb2db(value);
        }
        return super.addSignalValue(type, value);
    }

    private boolean decibelsPreferred(Signal type)
    {
        return (type == Signal.CDMA_ECIO || type == Signal.EVDO_ECIO) && preferDb;
    }
}
