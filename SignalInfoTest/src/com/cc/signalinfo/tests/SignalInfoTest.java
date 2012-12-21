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

import android.test.ActivityInstrumentationTestCase2;
import com.cc.signalinfo.SignalInfo;
import com.cc.signalinfo.util.SignalConstants;


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
public class SignalInfoTest extends ActivityInstrumentationTestCase2<SignalInfo>
{
    private String[] signalInfo = null;


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
    }

    public SignalInfoTest()
    {
        super(SignalInfo.class);
    }


    /**
     * Tests that filtering bad signal values only filters what it should and nothing else
     */
    public void testFilterSignalData()
    {
        String[] filteredSigInfo = SignalInfo.filterSignalData(signalInfo);

        for (int i = 0; i < filteredSigInfo.length; ++i) {
            if (i > 0 && i < 4) {
                assertEquals("Value should be " + SignalConstants.DEFAULT_TXT, SignalConstants.DEFAULT_TXT, filteredSigInfo[i]);
            }
            else {
                assertEquals("Value should be " + signalInfo[i], signalInfo[i], filteredSigInfo[i]);
            }
        }
    }
}
