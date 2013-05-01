package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.Map;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class LteInfo extends SignalInfo
{
    protected LteInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.LTE, tm, signals);
    }

    protected LteInfo(TelephonyManager tm)
    {
        super(NetworkType.LTE, tm);
    }

    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals.get(Signal.LTE_RSRP));
    }

}
