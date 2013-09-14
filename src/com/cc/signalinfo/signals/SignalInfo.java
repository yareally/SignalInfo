package com.cc.signalinfo.signals;

import android.telephony.TelephonyManager;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.StringUtils;

import java.util.*;

import static android.telephony.TelephonyManager.*;

/**
 * The parent class for each specific type of signal readings.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
 */
@SuppressWarnings({"MethodWithMultipleReturnPoints", "OverlyComplexMethod", "SwitchStatementWithTooManyBranches"})
public abstract class SignalInfo implements ISignal
{
    /**
     * Screw Android for using centibels when they should
     * be using decibels for things like SNR.
     *
     * if true, convert all non-decibel readings (centibels) to decibels
     */
    protected boolean preferDb = true;

    /**
     * The Possible values for the current network type
     */
    protected EnumSet<Signal> possibleValues = EnumSet.noneOf(Signal.class);
    /**
     * Holds all the signal values and key mappings for them
     */
    protected Map<Signal, String> signals;
    /**
     * The TelephonyManager for accessing some network stuff
     */
    protected TelephonyManager    tm;
    /**
     * The network type for the current SignalInfo instantiation
     */
    protected NetworkType         type;

    /**
     * Instantiates a new Signal info.
     *
     * @param type - the type of network
     * @param tm - instance of telephonyManager
     * @param signals - the signals to add
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
     * @param type - the type of network
     * @param tm - instance of telephonyManager
     * @param signals - the signals to add
     * @param preferDb - if true, convert all non-decibel readings (centibels) to decibels
     */
    protected SignalInfo(NetworkType type, TelephonyManager tm, Map<Signal, String> signals, boolean preferDb)
    {
        this(type, tm, signals);
        this.preferDb = preferDb;
    }

    /**
     * Instantiates a new Signal info.
     *
     * @param type - the type of network
     * @param tm - instance of telephonyManager
     */
    protected SignalInfo(NetworkType type, TelephonyManager tm)
    {
        this(type, tm, null);
    }

    /**
     * Gets the textual name for the type specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * Newer supported network types are near the bottom to avoid any issues with shitty old devices.
     *
     * @param tm - instance of telephonyManager
     * @return the given name for the network type the device is using currently for data
     */
    public static String getConnectedNetworkString(TelephonyManager tm)
    {
        switch (tm.getNetworkType()) {
            case NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            case NETWORK_TYPE_CDMA:
                return "CDMA";
            case NETWORK_TYPE_EDGE:
                return "EDGE";
            case NETWORK_TYPE_EVDO_0:
                return "Ev-DO rev. 0";
            case NETWORK_TYPE_EVDO_A:
                return "Ev-DO rev. A";
            case NETWORK_TYPE_GPRS:
                return "GPRS";
            case NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case NETWORK_TYPE_HSPA:
                return "HSPA";
            case NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case NETWORK_TYPE_UMTS:
                return "UMTS";
            case NETWORK_TYPE_IDEN:
                return "iDen";
            case NETWORK_TYPE_EVDO_B:
                return "Ev-DO rev. B";
            case NETWORK_TYPE_LTE:
                return "LTE";
            case NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case NETWORK_TYPE_HSPAP:
                return "HSPA+";
        }
        return "Unknown";
    }

    /**
     * Gets signal string given the SignalType
     *
     * @param signalType - the signalType
     * @return the signal string or null if doesn't exist
     */
    @Override
    public String getSignalString(Signal signalType)
    {
        return signals[signalType];
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
        float signalValue =
            AppSetup.DEFAULT_TXT.equals(signals[name])
                ? -1
                : Math.abs(Integer.parseInt(signals[name]));

        if (signalValue == -1) {
            return ""; // no value set
        }
        signalValue += name.norm(); // normalize the reading to align to zero
        float fudgeValue = 0;

        if (fudgeReading && name.fudged() > 0) {
            // since we normalize, one extrema has to be 0 and the other non-zero (like 80 or whatever)
            fudgeValue = name.best() > name.worst()
                ? 0 // for now, no need to fudge positive stuff like SNR
                : (name.worst() - signalValue) / 100.00f;
        }
        float result = name.best() > name.worst()
            ? signalValue / name.best() + fudgeValue
            : (name.worst() - signalValue) / name.worst() + fudgeValue;

        int percentSignal = Math.round(result * 100);
        percentSignal = percentSignal < 0 ? 0 : Math.abs(percentSignal);
        percentSignal = percentSignal > 100 ? 100 : percentSignal;

        return String.format("%s%%", percentSignal);
    }

    /**
     * Gets all the percentages of relative efficiency for the current network instead of just one.
     *
     * @param fudgeReading - set to true, fudge the reading to make the user feel better while ignoring standards
     * @return the % of all readings as a map of name of the reading as the key and the value as the value
     *
     * @see SignalInfo#getRelativeEfficiency(com.cc.signalinfo.enums.Signal, boolean)
     */
    @Override
    public Map<String, String> getRelativeEfficiencyMap(boolean fudgeReading)
    {
        Map<String, String> readings = new LinkedHashMap<>();

        for (Map.Entry<Signal, String> signalReading : signals.entrySet()) {
            readings[signalReading.getKey().name()] = getRelativeEfficiency(signalReading.getKey(), fudgeReading);
        }
        return readings;
    }

    /**
     * Gets the signal reading for the given signal type as an integer value
     *
     * @param name the name of the signal reading (RSSI, SNR, etc)
     * @return the signal reading (you better make sure it exists before getting it)
     */
    @Override
    public int get(Signal name)
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
            case PHONE_TYPE_GSM:
                return "GSM Device";
            case PHONE_TYPE_CDMA:
                return "GSM Device";
            case PHONE_TYPE_NONE:
                return "No Cellular Radio";
            case PHONE_TYPE_SIP:
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

    @Override
    public List<LinkedHashSet<String>> getStringSets()
    {
        List<LinkedHashSet<String>> signalSet = new ArrayList<>();
        LinkedHashSet<String> signalNames = new LinkedHashSet<>();
        LinkedHashSet<String> signalValues = new LinkedHashSet<>();

        for (Map.Entry<Signal, String> signal : signals.entrySet()) {
            signalNames.add(signal.getKey().name());
            signalValues.add(signal.getValue());
        }
        signalSet[0] = signalNames;
        signalSet[1] = signalValues;
        return signalSet;
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
        return signals[type] = value;
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

    /**
     * Converts a signal reading from centibels to decibels
     *
     * @param centibels - the signal reading in cB
     * @return the signal reading in dB
     */
    public static String cb2db(String centibels)
    {
        if (!StringUtils.isNullOrEmpty(centibels) && !AppSetup.DEFAULT_TXT.equals(centibels)) {
            centibels = String.valueOf((Integer.parseInt(centibels) / 10));
        }
        return centibels;
    }
}
