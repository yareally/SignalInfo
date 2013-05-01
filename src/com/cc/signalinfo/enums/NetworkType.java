package com.cc.signalinfo.enums;

/**
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
