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

package com.cc.signalinfo.tests;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.test.ActivityInstrumentationTestCase2;
import com.cc.signalinfo.activities.MainActivity;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;
import com.cc.signalinfo.util.SignalMapWrapper;
import com.cc.signalinfo.listeners.SignalListener;
import com.cc.signalinfo.signals.ISignal;
import com.cc.signalinfo.util.SignalArrayWrapper;

import java.util.EnumMap;
import java.util.Map;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.cc.signalinfo.SignalInfoTest \
 * com.cc.signalinfo.tests/android.test.InstrumentationTestRunner
 */
public class SignalInfoTest extends ActivityInstrumentationTestCase2<MainActivity> implements SignalListener.UpdateSignal
{
    private Activity                  activity;
    private Map<NetworkType, ISignal> networkMap;
    private SignalMapWrapper          signalMapWrapper;
    private String[] signalInfo = null;
    private TelephonyManager   tm;
    private SignalArrayWrapper signalArrayWrapper;

    public SignalInfoTest()
    {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        signalInfo = new String[]{
            "SignalStrength:",
            "99",
            "-1",
            "32123992",
            "-90",
            "-65",
            "7",
            "31",
            "-77",
            "3",
            "170",
            "-75",
            "gsm|lte"
        };

        activity = getActivity();
        SignalListener listener = new SignalListener(this);
        tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        networkMap = new EnumMap<>(NetworkType.class);
    }

    public void testGetInstance() throws Exception
    {
        assertNotNull("Test array was null. How the hell did that happen?? >:(", signalInfo);
    }

    /**
     * Tests that filtering bad signal values only filters what it should and nothing else
     */
    public void testFilterSignalData()
    {
        signalMapWrapper = signalArrayWrapper == null
            ? new SignalMapWrapper(signalInfo, tm)
            : new SignalMapWrapper(signalArrayWrapper.getFilteredArray(), tm);

        networkMap = signalMapWrapper.getNetworkMap();
        Signal[] values = Signal.values();

        for (Map.Entry<NetworkType, ISignal> networkType : networkMap.entrySet()) {
            for (int i = 1; i < signalInfo.length; ++i) {
                if (i > 0 && i < 4) {
                    assertEquals(
                        String.format("Value should be %s", AppSetup.DEFAULT_TXT),
                        AppSetup.DEFAULT_TXT,
                        networkType.getValue().getSignalString(values[i]));
                }
                else {
                    assertEquals(
                        String.format("Value should be %s", signalInfo[i]),
                        signalInfo[i],
                        networkType.getValue().getSignalString(values[i]));
                }
            }
        }
    }

    @Override
    public void setData(SignalArrayWrapper signalArrayWrapper)
    {
        this.signalArrayWrapper = signalArrayWrapper;
    }
}
