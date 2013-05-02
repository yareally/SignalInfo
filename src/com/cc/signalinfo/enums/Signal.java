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
 * /
 */

package com.cc.signalinfo.enums;

import static com.cc.signalinfo.enums.NetworkType.*;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public enum Signal
{
    /*    NONE(0),*/
    GSM_SIG_STRENGTH(1, GSM),
    GSM_BIT_ERROR(2, GSM),
    CDMA_RSSI(3, CDMA),
    CDMA_ECIO(4, CDMA),
    EVDO_RSSI(5, CDMA),
    EVDO_ECIO(6, CDMA),
    EVDO_SNR(7, CDMA),
    LTE_SIG_STRENGTH(8, LTE),
    LTE_RSRP(9, LTE),
    LTE_RSRQ(10, LTE),
    LTE_SNR(11, LTE),
    LTE_CQI(12, LTE),
    LTE_RSSI(13, LTE),
    GSM_RSSI(14, GSM),
    GSM_ASU(15, GSM);

    private int value;
    private NetworkType networkType;

    Signal(int value, NetworkType networkType)
    {
        this.value = value;
        this.networkType = networkType;
    }

    public NetworkType type()
    {
        return networkType;
    }
}
