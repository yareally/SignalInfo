/*
 *
 * Copyright (c) 2013 Wes Lanning, http://codingcreation.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 */

package com.cc.signalinfo.enums;

import static com.cc.signalinfo.enums.NetworkType.*;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public enum Signal implements SignalEnumMethods
{
    // best, worst, normalized are in dB
    GSM_SIG_STRENGTH(0, GSM, 31, 0, 0, 0),
    GSM_BIT_ERROR(1, GSM, 0, 7, 0, 0),
    CDMA_RSSI(2, CDMA, 0, 80, -40, 1),
    CDMA_ECIO(3, CDMA, 0, 16, 0, 1),
    EVDO_RSSI(4, CDMA, 0, 80, -40, 1),
    EVDO_ECIO(5, CDMA, 0, 16, 0, 1),
    EVDO_SNR(6, CDMA, 8, 0, 0, 0),
    LTE_SIG_STRENGTH(7, LTE, 31, 0, 0, 0),
    LTE_RSRP(8, LTE, 0, 76, -44, 1),
    LTE_RSRQ(9, LTE, 0, 17, -3, 0),
    LTE_SNR(10, LTE, 50, 0, 20, 0),
    LTE_CQI(11, LTE, 15, 0, 0, 0),
    LTE_RSSI(12, LTE, 0, 80, -40, 0),
    GSM_RSSI(13, GSM, 0, 62, -51, 1),
    GSM_ECIO(14, GSM, 0, 16, 0, 1);

    private       int         value;
    private       NetworkType networkType;
    private final int         best;
    private final int         worst;
    private       int         normalized;
    private       int         fudged;

    /**
     * Contains all the constants for the signal info
     *
     * @param value - the index for the value
     * @param networkType - the type of network the signal is for
     * @param best - the optimal theoretical value for the signal in dB
     * @param worst - the worst theoretical value for the signal in dB
     * @param normalized - how much the reading should be subtracted for calculating % in dB
     * @param fudged - should we pad the % result to make people feel better that their % is only like 63% when the reading is -60dBm lol (not their faults, it's a screwy range based on theoretical readings)
     */
    Signal(int value, NetworkType networkType, int best, int worst, int normalized, int fudged)
    {
        this.value = value;
        this.networkType = networkType;
        this.best = best;
        this.worst = worst;
        this.normalized = normalized;
        this.fudged = fudged;
    }

    @Override
    public NetworkType type()
    {
        return networkType;
    }

    @Override
    public int best()
    {
        return best;
    }

    @Override
    public int worst()
    {
        return worst;
    }

    @Override
    public int norm()
    {
        return normalized;
    }

    @Override
    public int value()
    {
        return value;
    }

    @Override
    public int fudged()
    {
        return fudged;
    }
}

