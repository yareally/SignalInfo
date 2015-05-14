package com.cc.signalinfo.signals

import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import java.util.{Map ⇒ Jmap, Set ⇒ Jset, LinkedHashSet ⇒ Lset, List ⇒ Jlist}

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
trait ISignal
{
    /**
     * Gets signal string.
     *
     * @param name the name
     * @return the signal string
     */
    def signalString(name: Signal): String

    /**
     * Gets signal.
     *
     * @param name the name
     * @return the signal
     */
    def get(name: Signal): Int

    /**
     * Gets the all the signal readings as a map.
     *
     * @return the signals
     */
    def getSignals: Jmap[Signal, String]

    /**
     * Is the current network type being used on the device?
     * Return of false means there's no signal currently, not that
     * the device cannot receive signals of this type of network.
     *
     * @return true if enabled
     */
    def enabled: Boolean

    /**
     * Gets network type.
     *
     * @return the network type
     */
    def getNetworkType: NetworkType

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
    def getStringSets: Jlist[Lset[String]]

    /**
     * Gets signal names.
     *
     * @return the signal names
     */
    def getSignalNames: Jset[Signal]

    /**
     * Gets the numerical value representing the specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * @return the Android API integer that represents the network type
     */
    def getConnectedNetworkValue: Int

    /**
     * Gets the textual name for the type specific type of
     * currently connected network (e.g. LTE, eHRPD, EV-DO, RTT, EDGE, etc)
     *
     * @return the given name for the network type the device is using currently for data
     */
    def getConnectedNetworkString: String

    /**
     * Get the primary device radio type as a numerical value defined by
     * Android OS.
     *
     * @return the Android API integer that represents the network type
     */
    def getDeviceType: Int

    /**
     * Get the primary device radio type as a string(GSM, CDMA, SIP, None)
     *
     * @return device type as a string
     */
    def getDeviceTypeString: String

    /**
     * Add a signal value to the current network type collection.
     *
     * @param signalType the type (like RSSI, RSRP, SNR, etc)
     * @param value the value (the current reading from the tower for the signal)
     * @return the value of any previous signal value with the
     *         specified type or null if there was no signal already added.
     */
    def addSignalValue(signalType: Signal, value: String): String

    /**
     * Does the current network type (gsm, cdma, etc)
     * contain the given type of signal?
     *
     * @return true if network type contains this type of signal
     */
    def containsSignalType(signalType: Signal): Boolean

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
    def relativeEfficiency(name: Signal, fudgeReading: Boolean): String

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
    def getRelativeEfficiencyMap(fudgeReading: Boolean): Jmap[String, String]

    /**
     * Number of signal readings contained in the class collection
     *
     * @return # of signal readings
     */
    def size: Int
}

