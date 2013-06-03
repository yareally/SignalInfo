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

package com.cc.signalinfo.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;
import com.cc.signalinfo.util.SignalArrayWrapper;

/**
 * Private helper class to listener for network signal changes.
 */
public class SignalListener extends PhoneStateListener
{
    private final String TAG = getClass().getSimpleName();
    private UpdateSignal listener;

    public SignalListener(UpdateSignal listener)
    {
        this.listener = listener;
    }
    /* TODO: think about making this a singleton if ever needed outside of just this file
       so we're not creating a bunch of telephony listeners
    */

    /**
     * Get the Signal strength from the provider, each time there is an update
     *
     * @param signalStrength - has all the useful signal stuff in it.
     */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
        super.onSignalStrengthsChanged(signalStrength);

        if (signalStrength != null) {
            listener.setData(new SignalArrayWrapper(signalStrength));
            Log.d(TAG, "getting sig strength");
            Log.d(TAG, signalStrength.toString());
        }
    }

    /**
     * Notifies activities and fragments of signal changes.
     */
    public interface UpdateSignal
    {
        /**
         * Set the data to return to the caller here
         *
         * @param signalStrength - data to return
         */
        void setData(SignalArrayWrapper signalStrength);
    }
}
