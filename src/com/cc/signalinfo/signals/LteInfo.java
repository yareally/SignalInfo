package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.config.SignalConstants;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * The type Lte info.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class LteInfo extends SignalInfo
{
    /**
     * Instantiates a new Lte info.
     *
     * @param tm the tm
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
     * @param tm the tm
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
        return !StringUtils.isNullOrEmpty(signals.get(Signal.LTE_RSRP))
            && !StringUtils.isNullOrEmpty(signals.get(Signal.LTE_RSRQ))
            && !SignalConstants.DEFAULT_TXT.equals(signals.get(Signal.LTE_RSRP))
            && !SignalConstants.DEFAULT_TXT.equals(signals.get(Signal.LTE_RSRQ));
    }

    /**
     * Computes the LTE RSSI by what is most likely the default number of
     * channels on the LTE device (at least for Verizon).
     *
     * @return the RSSI signal
     */
    private int computeRssi()
    {
        return -(-17 - Integer.parseInt(signals.get(Signal.LTE_RSRP)) - Integer.parseInt(signals.get(Signal.LTE_RSRQ)));
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
        return !StringUtils.isNullOrEmpty(signals.get(Signal.LTE_RSRP));
    }

    /**
     * Add signal value.
     *
     * @param type the type
     * @param value the value
     * @return the value of any previous signal value with the
     *         specified type or null if there was no signal already added.
     */
    @Override
    public String addSignalValue(Signal type, String value)
    {
        String oldValue = super.addSignalValue(type, value);
        // if we can now add RSSI, do. Have to manually calculate it though
        if (hasLteRssi()) {
            super.addSignalValue(Signal.LTE_RSSI, String.valueOf(computeRssi()));
        }
        return oldValue;
    }
}
