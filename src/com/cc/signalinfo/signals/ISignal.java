package com.cc.signalinfo.signals;

import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

import java.util.Map;
import java.util.Set;

/**
 * The interface I signal.
 *
 * @author Wes Lanning
 * @version 2013 -04-29
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
    int getSignal(Signal name);

    /**
     * Gets signals.
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
}
