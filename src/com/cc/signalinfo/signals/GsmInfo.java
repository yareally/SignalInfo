package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * The type Gsm info.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
public class GsmInfo extends SignalInfo
{
    /**
     * Instantiates a new Gsm info.
     *
     * @param tm the tm
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
     * @param tm the tm
     */
    public GsmInfo(TelephonyManager tm)
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
        return !StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH));
    }
}
