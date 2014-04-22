package com.cc.signalinfo.enums;

/**
 * @author Wes Lanning
 * @version 2013-09-01
 */
public interface SignalEnumMethods
{
    NetworkType type();

    int best();

    int worst();

    int norm();

    int value();

    int fudged();
}
