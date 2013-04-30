package com.cc.signalinfo.signals;

import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public abstract class SignalInfo implements ISignal
{
    protected Map<Signal, String> signals;
    protected NetworkType type;

    protected SignalInfo(NetworkType type, Map<Signal, String> signals)
    {
        this.type = type;
        this.signals = signals == null
            ? new EnumMap<Signal, String>(Signal.class)
            : new EnumMap<Signal, String>(signals);
    }

    protected SignalInfo(NetworkType type)
    {
        this(type, null);
    }

    @Override
    public String getSignalString(Signal name)
    {
        return signals.get(name);
    }

    @Override
    public int getSignal(Signal name)
    {
        return Integer.parseInt(signals.get(name));
    }

    @Override
    public Map<Signal, String> getSignals()
    {
        return Collections.unmodifiableMap(signals);
    }

    @Override
    public Set<Signal> getSignalNames()
    {
        return signals.keySet();
    }

    @Override
    public NetworkType getNetworkType()
    {
        return type;
    }

/*    @Override
    public abstract boolean enabled();*/

}
