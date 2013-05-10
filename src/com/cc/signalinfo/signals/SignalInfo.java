package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

import java.util.*;

/**
 * The type Signal info.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
@SuppressWarnings({"MethodWithMultipleReturnPoints", "OverlyComplexMethod", "SwitchStatementWithTooManyBranches"})
public abstract class SignalInfo implements ISignal
{
    /**
     * The TelephonyManager for accessing some network stuff
     */
    protected TelephonyManager    tm;
    /**
     * Holds all the signal values and key mappings for them
     */
    protected Map<Signal, String> signals;
    /**
     * The network type for the current SignalInfo instantiation
     */
    protected NetworkType         type;
    /**
     * The Possible values for the current network type
     */
    protected EnumSet<Signal> possibleValues = EnumSet.noneOf(Signal.class);

    /**
     * Instantiates a new Signal info.
     *
     * @param type the type
     * @param tm the tm
     * @param signals the signals
     */
    protected SignalInfo(NetworkType type, TelephonyManager tm, Map<Signal, String> signals)
    {
        this.type = type;
        this.tm = tm;
        this.signals = signals == null
            ? new EnumMap<Signal, String>(Signal.class)
            : new EnumMap<>(signals);
    }

    /**
     * Instantiates a new Signal info.
     *
     * @param type the type
     * @param tm the tm
     */
    protected SignalInfo(NetworkType type, TelephonyManager tm)
    {
        this(type, tm, null);
    }

    /**
     * Gets signal string given the SignalType
     *
     * @param signalType the signalType
     * @return the signal string or null if doesn't exist
     */
    @Override
    public String getSignalString(Signal signalType)
    {
        return signals.get(signalType);
    }

    /**
     * The percent from 0 (worst) 100 (best)
     * of how great the current signal measurement is.
     *
     * May be imprecise due to carrier differences for
     * certain measures (like RSSI), but this is more
     * user friendly for those not interested in what
     * the measures actually mean and their measurement range.
     *
     * @param name - the name of the reading to compute
     * @param fudgeReading - set to true, fudge the reading to make the user feel better while ignoring standards
     * @return the relative efficiency as a percent
     */
    @Override
    public String getRelativeEfficiency(Signal name, boolean fudgeReading)
    {
        int signalValue =
            AppSetup.DEFAULT_TXT.equals(signals.get(name))
            ? -1
            : Math.abs(Integer.parseInt(signals.get(name)));

        if (signalValue == -1) {
            return ""; // no value set
        }
        int fudged = fudgeReading ? name.best() + name.fudged() : name.best();
        signalValue += name.norm(); // normalize the reading to align to zero

        float result = name.best() > name.worst()
            ? (float)signalValue / fudged
            : 1 - (float)signalValue / name.worst();
        // LTE_RSRQ(10, LTE, 0, 17, 3, 0), // -20db is coming out as 16%, should be 0

        int percentSignal = Math.round(result * 100);
        percentSignal = percentSignal < 0 ? 0 : Math.abs(percentSignal);

        return String.format("%s%%", percentSignal);
    }

    /**
     * Gets the signal reading for the given signal type as an integer value
     *
     * @param name the name of the signal reading (RSSI, SNR, etc)
     * @return the signal reading (you better make sure it exists before getting it)
     */
    @Override
    public int getSignal(Signal name)
    {
        return Integer.parseInt(signals.get(name));
    }

    /**
     * Gets all the signal types and readings for the current
     * network collection.
     *
     * @return the signals
     */
    @Override
    public Map<Signal, String> getSignals()
    {
        return Collections.unmodifiableMap(signals);
    }

    /**
     * Gets signal type names for the currently stored
     * signal readings.
     *
     * @return the signal names
     */
    @Override
    public Set<Signal> getSignalNames()
    {
        return signals.keySet();
    }

    /**
     * Does the current network type (gsm, cdma, etc)
     * contain the given type of signal?
     *
     * @param type the type
     * @return true if network type contains this type of signal
     */
    @Override
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
     * @param tm the tm
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
     * Gets the network type for this SignalType instantiation
     *
     * @return the network type
     */
    @Override
    public NetworkType getNetworkType()
    {
        return type;
    }

    /**
     * Add a signal value to the current network type collection.
     *
     * @param type the type (like RSSI, RSRP, SNR, etc)
     * @param value the value (the current reading from the tower for the signal)
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
