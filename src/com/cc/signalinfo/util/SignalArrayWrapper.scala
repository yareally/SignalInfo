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
import java.util.regex.Pattern

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
    private final val FILTER_SIGNAL : Pattern = Pattern.compile("^0|^-1|^-?99|^-?[1-9][0-9]{3,}")
    private final val HAS_LETTERS   : Pattern = Pattern.compile("[^\\d-\\s]+")
    private final val ICS_ARRAY_SIZE: Int     = 12
    private final val LEGACY_DEVICE : Pattern = Pattern.compile("[ .,|:]+")
    private final val SPACE_STR     : Pattern = Pattern.compile(" ")
    private       var gsmPos        : Int     = -1

    /**
     * Set the signal info the user sees.
     *
     * @param rawData - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private def processSignalInfo(rawData: Array[String]): Array[String] =
    {
        val endPos: Int = findIsGsmPos(rawData)
        val filteredData = new Array[String](ICS_ARRAY_SIZE)

        for (i ← 1 to filteredData.length) {
            filteredData(i - 1) =
                if (i >= endPos || rawData(i) == null || FILTER_SIGNAL.matcher(rawData(i)).matches) {
                    AppSetup.DEFAULT_TXT
                }
                else {
                    rawData(i)
                }
        }

        Log.d("Raw Signal Array", rawData.mkString(","))
        Log.d("Filtered Signal Array", rawData.mkString(","))
        return filteredData
    }

    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see android.telephony.SignalStrength for more info.
     */
    private def processSignalInfo(signalStrength: SignalStrength): Array[String] =
    {
        var splitSignals: Array[String] = SPACE_STR.split(signalStrength.toString)
        if (splitSignals.length < 5) {
            splitSignals = LEGACY_DEVICE.split(signalStrength.toString)
        }
        return processSignalInfo(splitSignals)
    }

    /**
     * Finds the position of the gsm|lte or cdma string in the array
     *
     * This is mainly for dealing with shitty old devices that don't have all the LTE API stuff.
     * Since those devices will have less fields in their raw data array, we must account for that.
     * Yes, it's not optimal to pretend they could have LTE by adding the values,
     * but fuck these devices. Especially since it's possible for 2.3 and 2.2 devices to have LTE,
     * which complicates things more. I prefer my sanity and letting them eat up a little more memory
     * on their craptastic device than running more checks later on.
     *
     * If you own one of these devices and are reading this, forgive the rage, but
     * things like this on Android frustrate the hell out of me and as a reader,
     * I assume you are developmentally inclined and can commiserate.
     *
     * Also, a happy developer is a good a good developer :)
     *
     * @param signalArray - the array to search
     * @return the position or -1 (not really possible) if not found
     */
    private def findIsGsmPos(signalArray: Array[String]): Int =
    {
        if (gsmPos == -1 || gsmPos > signalArray.length - 1) {
            for (i ← signalArray.length - 1 to 0 by -1) {
                if (HAS_LETTERS.matcher(signalArray(i)).matches) {
                    gsmPos = i
                    return i
                }
            }
            gsmPos = if (gsmPos == -1 && signalArray.length - 1 > 0) signalArray.length - 1 else 0
        }
        return gsmPos
    }
}

/**
 * Wraps the raw signal data array produced by the system in order
 * to return a uniform signal array that follows ICS+ standards.
 *
 * @param signalStrength - contains the raw signal info reported from the system
 */
class SignalArrayWrapper(signalStrength: SignalStrength)
{
    private val filteredArray: Array[String] = SignalArrayWrapper.processSignalInfo(signalStrength)
    private val rawData      : String        = signalStrength.toString

    /**
     * Returns a copy (not a reference) of the signal array after
     * being reformatted to meet ICS+ expectations.
     *
     * @return the processed signal array in the form one expects in ICS+
     */
    def getFilteredArray: Array[String] = {
        return Arrays.copyOf(filteredArray, filteredArray.length)
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



