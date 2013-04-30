package com.cc.signalinfo.signals;

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
    protected LteInfo(Map<Signal, String> signals)
    {
        super(NetworkType.LTE, signals);
    }

    protected LteInfo()
    {
        super(NetworkType.LTE);
    }

    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals.get(Signal.LTE_RSRP));
    }

}
