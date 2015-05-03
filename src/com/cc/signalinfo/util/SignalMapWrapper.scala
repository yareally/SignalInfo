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
package com.cc.signalinfo.util

import android.telephony.TelephonyManager
import android.util.Log
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.enums.NetworkType
import com.cc.signalinfo.enums.Signal
import com.cc.signalinfo.signals.CdmaInfo
import com.cc.signalinfo.signals.GsmInfo
import com.cc.signalinfo.signals.ISignal
import com.cc.signalinfo.signals.LteInfo
import java.util.{LinkedHashMap ⇒ Lmap, Map ⇒ Jmap, EnumMap ⇒ Emap, Collections}

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
object SignalMapWrapper
{
    /**
     * Initialize the network map that will hold all the various signal readings
     *
     * @param tm - dependency for the network map
     * @return the created map, empty other than the signal container maps (for gsm, lte, etc)
     */
    private def initNetworkMap(tm: TelephonyManager): Jmap[NetworkType, ISignal] = {
        val networkMap = new Emap[NetworkType, ISignal](classOf[NetworkType])
        networkMap.put(NetworkType.CDMA, new CdmaInfo(tm))
        networkMap.put(NetworkType.LTE, new LteInfo(tm))
        networkMap.put(NetworkType.GSM, new GsmInfo(tm))
        networkMap
    }
}

/**
 *
 * @param pNetworkMap - network map for the wrapper
 */
class SignalMapWrapper(pNetworkMap: Jmap[NetworkType, ISignal])
{
    private var networkMap = if (pNetworkMap != null) new Emap[NetworkType, ISignal](pNetworkMap) else pNetworkMap

    /**
     * @param filteredSignalData - signal data formatted for ICS+ compatibility
     * @param tm - instance of TelephonyManager
     */
    def this(filteredSignalData: Array[String], tm: TelephonyManager) {
        this(null)
        networkMap = createSignalDataMap(tm, filteredSignalData)
    }

    /**
     * Do we already have signal data entered from the system?
     *
     * @return true if we already have at least one signal data reading collected.
     */
    def hasData: Boolean = {
        !networkMap.isEmpty && networkMap.entrySet.iterator.hasNext
    }

    /**
     * Returns an unmodifiable copy of the network signal info map
     *
     * @return network signal info organized by gsm, cdma, lte, etc
     */
    def getNetworkMap: Jmap[NetworkType, ISignal] = {
        Collections.unmodifiableMap(networkMap)
    }

    /**
     * Returns a copy of the network signal info map
     * with the % quality of each signal instead of decibels.
     *
     * @param adjustReadings - use strict % readings or adjust for carriers/android?
     * @return network signal info organized by gsm, cdma, lte, etc
     */
    def getPercentSignalMap(adjustReadings: Boolean): Jmap[String, String] = {
        import scala.collection.JavaConversions._
        val percentSignalMap = new Lmap[String, String]

        for (network ← networkMap.entrySet) {
            percentSignalMap.putAll(network.getValue.getRelativeEfficiencyMap(adjustReadings))
        }
        percentSignalMap
    }

    /**
     * Maps the radio signal readings to corresponding network type.
     * Then returns the created map.
     *
     * @param data - signal data to add to a map of network (key), signal reading (value) pairs
     * @param tm - dependency for the network map
     * @return filtered data with "n/a" instead of the bad value
     */
    private def createSignalDataMap(tm: TelephonyManager, data: Array[String]): Jmap[NetworkType, ISignal] = {
        val networkMapCopy = SignalMapWrapper.initNetworkMap(tm)
        val values: Array[Signal] = Signal.values

        for (i ← 0 until values.length) {
            val signalValue: String = if (i < data.length) data(i) else AppSetup.DEFAULT_TXT
            networkMapCopy.get(values(i).`type`).addSignalValue(values(i), signalValue)
        }

        Log.d("Signal Map CDMA: ", networkMapCopy.get(NetworkType.CDMA).getSignals.toString)
        Log.d("Signal Map GSM: ", networkMapCopy.get(NetworkType.GSM).getSignals.toString)
        Log.d("Signal Map LTE: ", networkMapCopy.get(NetworkType.LTE).getSignals.toString)
        networkMapCopy
    }


}

