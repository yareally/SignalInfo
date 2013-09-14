package com.cc.signalinfo.signals;

import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface ISignal. Why is it called ISignal? Because I couldn't
 * come up with a better name without screwing up proper naming of the
 * enum of similar name.
 *
 * This just serves as an interface to access the LTE/GSM/CDMA signals
 * through polymorphism.
 *
 * @author Wes Lanning
 * @version 2013-04-29
 */
public interface ISignal
{
    /**
     * Gets signal string.
     *
     * @param name the name
     * @return the signal string
     */
    String getSignalString(Signal name);

    /**
     * Gets signal.
     *
     * @param name the name
     * @return the signal
     */
    int get(Signal name);

    /**
     * Gets the all the signal readings as a map.
     *
     * @return the signals
     */
    Map<Signal, String> getSignals();

    /**
     * Is the current network type being used on the device?
     * Return of false means there's no signal currently, not that
     * the device cannot receive signals of this type of network.
     *
     * @return true if enabled
     */
    boolean enabled();

    /**
     * Gets network type.
     *
     * @return the network type
     */
    NetworkType getNetworkType();

    /**
     * Returns two arrays of linked hash sets. Values are
     * in order for each array and align so name1 = value1, etc
     *
     * array 1: the names of the signals
     *
     * array 2: the values of the signals
     *
     * @return the set mainly used so these can be stored in sharedPreferences.
     */
    List<LinkedHashSet<String>> getStringSets();

    /**
     * Gets signal names.
     *
     * @return the signal names
     */
    Set<Signal> getSignalNames();

    /**
     * Gets the numerical value representing the specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * @return the Android API integer that represents the network type
     */
    int getConnectedNetworkValue();

    /**
     * Gets the textual name for the type specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * @return the given name for the network type the device is using currently for data
     */
    String getConnectedNetworkString();

    /**
     * Get the primary device radio type as a numerical value defined by
     * Android OS.
     *
     * @return the Android API integer that represents the network type
     */
    int getDeviceType();

    /**
     * Get the primary device radio type as a string(GSM, CDMA, SIP, None)
     *
     * @return device type as a string
     */
    String getDeviceTypeString();

    /**
     * Add a signal value to the current network type collection.
     *
     * @param type the type (like RSSI, RSRP, SNR, etc)
     * @param value the value (the current reading from the tower for the signal)
     * @return the value of any previous signal value with the
     *         specified type or null if there was no signal already added.
     */
    String addSignalValue(Signal type, String value);

    /**
     * Does the current network type (gsm, cdma, etc)
     * contain the given type of signal?
     *
     * @return true if network type contains this type of signal
     */
    boolean containsSignalType(Signal type);

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
    String getRelativeEfficiency(Signal name, boolean fudgeReading);

    /**
     * The percent from 0 (worst) 100 (best)
     * of how great each measurement for the current network is
     *
     * May be imprecise due to carrier differences for
     * certain measures (like RSSI), but this is more
     * user friendly for those not interested in what
     * the measures actually mean and their measurement range.
     *
     * @param fudgeReading - set to true, fudge the reading to make the user feel better while ignoring standards
     * @return the % of all readings as a map of name of the reading as the key and the value as the value
     */
    public Map<String, String> getRelativeEfficiencyMap(boolean fudgeReading);

    /**
     * Number of signal readings contained in the class collection
     *
     * @return # of signal readings
     */
    int size();
}
