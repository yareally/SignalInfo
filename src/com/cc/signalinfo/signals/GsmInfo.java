package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class GsmInfo extends SignalInfo
{
    public GsmInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.GSM, tm, signals);
        possibleValues =
            EnumSet.of(Signal.GSM_SIG_STRENGTH, Signal.GSM_RSSI, Signal.GSM_ASU, Signal.GSM_BIT_ERROR);
    }

    public GsmInfo(TelephonyManager tm)
    {
        this(tm, null);
    }

    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH));
    }
}
