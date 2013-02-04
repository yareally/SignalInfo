/*
Copyright (c) 2012 Wes Lanning, http://codingcreation.com

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

http://www.opensource.org/licenses/mit-license.php
*/

package com.cc.signalinfo.util;

/**
 * @author Wes Lanning
 * @version 2012-12-21
 */
public final class SignalConstants
{
    public static final int GSM_SIG_STRENGTH = 1;
    public static final int GSM_BIT_ERROR    = 2;
    public static final int CDMA_SIGNAL      = 3;
    public static final int                  CDMA_ECIO          = 4;
    public static final int                  EVDO_SIGNAL        = 5;
    public static final int                  EVDO_ECIO          = 6;
    public static final int                  EVDO_SNR           = 7;
    public static final int                  LTE_SIG_STRENGTH   = 8;
    public static final int                  LTE_RSRP           = 9;
    public static final int                  LTE_RSRQ           = 10;
    public static final int                  LTE_SNR            = 11;
    public static final int                  LTE_CQI            = 12;
    public static final int                  IS_GSM             = 13;
    public static final int                  LTE_RSSI           = 14;
    public static final String               DEFAULT_TXT        = "N/A";

    /**
    * Key name for the stored preference that checks if a user agreed to warning prompt screen
     */
    public static final String PROMPT_SETTING = "promptWarningDialog";
}
