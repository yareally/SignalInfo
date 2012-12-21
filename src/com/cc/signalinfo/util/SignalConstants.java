package com.cc.signalinfo.util;

/**
 * User: admin
 * Date: 12/21/12
 * Time: 1:11 AM
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
    public static final int                  MAX_SIGNAL_ENTRIES = 14;

    /**
    * Key name for the stored preference that checks if a user agreed to warning prompt screen
     */
    public static final String PROMPT_SETTING = "promptWarningDialog";
}
