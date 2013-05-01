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
public class GsmInfo extends SignalInfo
{
    protected GsmInfo(TelephonyManager tm, Map<Signal, String> signals)
    {
        super(NetworkType.GSM, tm, signals);
    }

    protected GsmInfo(TelephonyManager tm)
    {
        super(NetworkType.GSM, tm);
    }

    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals.get(Signal.GSM_SIG_STRENGTH));
    }

}
