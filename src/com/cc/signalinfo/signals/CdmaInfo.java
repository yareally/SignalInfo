package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * The type Cdma info.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class CdmaInfo extends SignalInfo
{
    /**
     * Instantiates a new Cdma info.
     *
     * @param tm the tm
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
     * @param tm the tm
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
}
