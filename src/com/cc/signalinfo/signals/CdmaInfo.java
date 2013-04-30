package com.cc.signalinfo.signals;

import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.Map;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class CdmaInfo extends SignalInfo
{
    protected CdmaInfo(Map<Signal, String> signals)
    {
        super(NetworkType.CDMA, signals);
    }

    protected CdmaInfo()
    {
        super(NetworkType.CDMA);
    }

    @Override
    public boolean enabled()
    {
        return !StringUtils.isNullOrEmpty(signals.get(Signal.CDMA_RSSI))
            || !StringUtils.isNullOrEmpty(signals.get(Signal.EVDO_RSSI));
    }

}
