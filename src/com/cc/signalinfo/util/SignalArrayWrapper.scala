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
package com.cc.signalinfo.util

import android.telephony.SignalStrength
import android.util.Log
import com.cc.signalinfo.config.AppSetup
import java.util.regex.{Pattern}

/**
 * Wraps the raw signal data in order to filter
 * out invalid values as well as make the array compatible with ICS+
 * for shitty old devices from 2.3 and before
 * (though HSPA+ was not officially added until ICS, so technically 3.0 too).
 *
 * @author Wes Lanning
 * @version 2013-05-10
 */
object SignalArrayWrapper
{
    final         val EMPTY_SIGNAL_ARRAY   : Array[String] = new Array[String](0)
    private final val FILTER_INVALID_SIGNAL: Pattern       = Pattern.compile("-1\\b|-?99\\b|0x[\\d]+|-?[4-9][0-9]{3,}|-?[0-9]{4,}")
    private final val FILTER_NON_NUM       : Pattern       = Pattern.compile("\\s?[^- \\d]+", Pattern.CASE_INSENSITIVE)
    private final val FILTER_SIGNAL        : Pattern = Pattern.compile("^0|^-1|^-?99|^-?[1-9][0-9]{3,}")
    private final val ICS_ARRAY_SIZE       : Int     = 12
    private final val ICS_BIG_ARRAY_SIZE   : Int     = 14
    private final val LEGACY_BIG_ARRAY_SIZE: Int     = 10
    private final val SPACE_STR    : Pattern                     = Pattern.compile(" ")
    private final val TAG          : String                      = classOf[SignalArrayWrapper].getSimpleName
}

/**
 * Wraps the raw signal data array produced by the system in order
 * to return a uniform signal array that follows ICS+ standards.
 *
 * @param signalArray - contains the raw signal info reported from the system
 */
class SignalArrayWrapper(signalArray: String)
{
    import SignalArrayWrapper._

    private val rawData: String = signalArray
    private var filteredArray: Array[String] = EMPTY_SIGNAL_ARRAY


    /**
     * Wraps the raw signal data array produced by the system in order

     *
    to return a uniform signal array that follows ICS+ standards.
     *
     * @param signalStrength - contains the raw signal info reported from the system
     */
    def this(signalStrength: SignalStrength) {
        this(signalStrength.toString)
    }

    /**
     * Returns a copy (not a reference) of the signal array after
     * being reformatted to meet ICS+ expectations.
     *
     * @return the processed signal array in the form one expects in ICS+
     */
    def getFilteredArray: Array[String] = {
        val arrayCopy: Array[String] = new Array[String](filteredArray.length)
        System.arraycopy(filteredArray, 0, arrayCopy, 0, arrayCopy.length)
        return arrayCopy
    }

    def filterSignals(rawData: String): Array[String] = {
        // remove all invalid signals and put in our default string instead to make life easier

        Log.d(TAG, s"rawData: $rawData")
        var filteredData: String = FILTER_NON_NUM.matcher(rawData).replaceAll("").trim
        Log.d(TAG, s"filtered after 1st regex: $filteredData")

        filteredData = FILTER_INVALID_SIGNAL.matcher(filteredData).replaceAll(AppSetup.DEFAULT_TXT)
        Log.d(TAG, s"filtered after 2nd regex: $filteredData")

        val splitSignals: Array[String] = SPACE_STR.split(filteredData)
        Log.d(TAG, s"splitsignals: ${splitSignals.toString}")

        // TODO: fix stupid devices like Huawai and LG that do LTE_RSSI = LTE_Signal_Strength
        var extendedSignalData: Array[String] = new Array[String](ICS_BIG_ARRAY_SIZE)
        extendedSignalData = Arrays.copyOf(splitSignals, extendedSignalData.length)

        if (splitSignals.length < extendedSignalData.length) {
            java.util.Arrays.fill(extendedSignalData.asInstanceOf[Array[Object]], splitSignals.length, extendedSignalData.length, AppSetup.DEFAULT_TXT)
        }
        Log.d(TAG, s"Extended Filtered Signal Data: ${extendedSignalData.toString}")
        filteredArray = extendedSignalData
        return extendedSignalData
    }

    /**
     * Gets the pre-filtered data from the signal array.
     * Useful really only for debugging/testing purposes.
     *
     * @return raw signal data
     */
    def getRawData: String = {
        return rawData
    }
}



