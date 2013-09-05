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
    /*    NONE(0),*/
    GSM_SIG_STRENGTH(1, GSM, 31, 0, 0, 0),
    GSM_BIT_ERROR(2, GSM, 0, 7, 0, 0),
    CDMA_RSSI(3, CDMA, 0, 80, -40, 20),
    CDMA_ECIO(4, CDMA, 0, 160, 0, 20),
    EVDO_RSSI(5, CDMA, 0, 80, -40, 20),
    EVDO_ECIO(6, CDMA, 0, 160, 0, 20),
    EVDO_SNR(7, CDMA, 8, 0, 0, 0),
    LTE_SIG_STRENGTH(8, LTE, 31, 0, 0, 0),
    LTE_RSRP(9, LTE, 0, 76, -44, 22),
    LTE_RSRQ(10, LTE, 0, 17, -3, 0),
    LTE_SNR(11, LTE, 500, 0, 200, 0),
    LTE_CQI(12, LTE, 15, 0, 0, 0),
    LTE_RSSI(13, LTE, 0, 90, -30, 15),
    GSM_RSSI(14, GSM, 0, 62, -51, 25),
    GSM_ASU(15, GSM, 31, 0, 0, 0);

    private       int         value;
    private       NetworkType networkType;
    private final int         best;
    private final int         worst;
    private       int         normalized;
    private int fudged;

    /**
     * Contains all the constants for the signal info
     *
     * @param value - the index for the value
     * @param networkType - the type of network the signal is for
     * @param best - the optimal theoretical value for the signal
     * @param worst - the worst theoretical value for the signal
     * @param normalized - how much the reading should be subtracted for calculating %
     * @param fudged - how much to subtract instead to make people feel better that their % is only like 63% when the reading is -60dBm lol
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
