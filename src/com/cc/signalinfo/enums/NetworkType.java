package com.cc.signalinfo.enums;

/**
 * Holds a reference to all the various networks a device may use
 *
 * @author Wes Lanning
 * @version 2013-04-29
 */
public enum NetworkType
{
    GSM(1),
    CDMA(2),
    LTE(3),
    UNKNOWN(0);

    private int value;

    NetworkType(int value)
    {
        this.value = value;
    }
}
