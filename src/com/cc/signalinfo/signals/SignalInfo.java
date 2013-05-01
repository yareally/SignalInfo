package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

import java.util.*;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
@SuppressWarnings({"MethodWithMultipleReturnPoints", "OverlyComplexMethod", "SwitchStatementWithTooManyBranches"})
public abstract class SignalInfo implements ISignal
{
    protected TelephonyManager    tm;
    protected Map<Signal, String> signals;
    protected NetworkType         type;
    protected EnumSet<Signal> possibleValues = EnumSet.noneOf(Signal.class);

    protected SignalInfo(NetworkType type, TelephonyManager tm, Map<Signal, String> signals)
    {
        this.type = type;
        this.tm = tm;
        this.signals = signals == null
            ? new EnumMap<Signal, String>(Signal.class)
            : new EnumMap<Signal, String>(signals);
    }

    protected SignalInfo(NetworkType type, TelephonyManager tm)
    {
        this(type, tm, null);
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

    /**
     * Does the current network type (gsm, cdma, etc)
     * contain the given type of signal?
     *
     * @return true if network type contains this type of signal
     */
    public boolean containsSignalType(Signal type)
    {
        return possibleValues.contains(type);
    }

    /**
     * Gets the numerical value representing the specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * @return the Android API integer that represents the network type
     */
    @Override
    public int getConnectedNetworkValue()
    {
        return tm.getNetworkType();
    }

    /**
     * Gets the textual name for the type specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * Newer supported network types are near the bottom to avoid any issues with shitty old devices.
     *
     * @return the given name for the network type the device is using currently for data
     */
    public static String getConnectedNetworkString(TelephonyManager tm)
    {
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "Ev-DO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "Ev-DO rev. A";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "Ev-DO rev. B";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
        }
        return "Unknown";
    }

    /**
     * Gets the textual name for the type specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * Newer supported network types are near the bottom to avoid any issues with shitty old devices.
     *
     * @return the given name for the network type the device is using currently for data
     */
    @Override
    public String getConnectedNetworkString()
    {
        return getConnectedNetworkString(tm);
    }

    /**
     * Get the primary device radio type as a numerical value defined by
     * Android OS.
     *
     * @return the Android API integer that represents the network type
     */
    @Override
    public int getDeviceType()
    {
        return tm.getPhoneType();
    }

    /**
     * Get the primary device radio type as a string(GSM, CDMA, SIP, None)
     *
     * @return device type as a string
     */
    @Override
    public String getDeviceTypeString()
    {
        switch (tm.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM Device";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "GSM Device";
            case TelephonyManager.PHONE_TYPE_NONE:
                return "No Cellular Radio";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "Voice over IP (VoIP)";
        }
        return "No Cellular Radio";
    }

    /**
     * Gets network type.
     *
     * @return the network type
     */
    @Override
    public NetworkType getNetworkType()
    {
        return type;
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
        return signals.put(type, value);
    }

    /**
     * Number of signal readings contained in the class collection
     *
     * @return # of signal readings
     */
    @Override
    public int size()
    {
        return signals.size();
    }

/*    @Override
    public abstract boolean enabled();*/
}
