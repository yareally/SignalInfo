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

package com.cc.signalinfo.tests;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import com.cc.signalinfo.activities.MainActivity;
import com.cc.signalinfo.util.SettingsHelpers;

import java.util.Collection;
import java.util.HashSet;

import static com.cc.signalinfo.tests.TestHelpers.errorMsg;

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
@SuppressWarnings({"ProhibitedExceptionDeclared", "FeatureEnvy"})
public class SharedPreferenceTest extends ActivityInstrumentationTestCase2<MainActivity>
{
    Context context;
    Activity activity;

    public SharedPreferenceTest()
    {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        context = getInstrumentation().getTargetContext().getApplicationContext();
        activity = getActivity();
    }

    public void testGetInstance() throws Exception
    {
        assertNotNull("Test activity was null. How the hell did that happen?? >:(", context);
    }

    /**
     * Tests that filtering bad signal values only filters what it should and nothing else
     */
    public void testGettingBool() throws Exception
    {
        boolean expected = true;
        boolean result = SettingsHelpers.addSharedPreference(activity, "test", expected);
        assertTrue(errorMsg(expected, false), result);

        boolean actual = SettingsHelpers.getPreference(activity, "test", false);
        assertTrue(errorMsg(expected, actual), actual);
    }

    public void testGettingString() throws Exception
    {
        String expected = "String to test";
        boolean result = SettingsHelpers.addSharedPreference(activity, "testString", expected);
        assertTrue(errorMsg("true", result), result);

        String actual = SettingsHelpers.getPreference(activity, "testString", "");
        assertEquals(errorMsg(expected, actual), expected, actual);
    }

    public void testGettingInteger() throws Exception
    {
        int expected = 1200;
        boolean result = SettingsHelpers.addSharedPreference(activity, "testString", expected);
        assertTrue(errorMsg("true", result), result);

        int actual = SettingsHelpers.getPreference(activity, "testString", -1);
        assertEquals(errorMsg(expected, actual), expected, actual);
    }

    public void testGettingFloat() throws Exception
    {
        float expected = 1200.11f;
        boolean result = SettingsHelpers.addSharedPreference(activity, "testString", expected);
        assertTrue(errorMsg("true", result), result);

        float actual = SettingsHelpers.getPreference(activity, "testString", -1.0f);
        assertEquals(errorMsg(expected, actual), expected, actual);
    }

    public void testGettingSet() throws Exception
    {
        Collection<String> expected = new HashSet<String>(5);
        expected.add("test");
        expected.add("test 2");

        boolean result = SettingsHelpers.addSharedPreference(activity, "testString", expected);
        assertTrue(errorMsg("true", result), result);

        Collection<String> actual = SettingsHelpers.getPreference(activity, "testString", new HashSet<String>(0));
        assertEquals(errorMsg(expected, actual), expected, actual);
        assertEquals(errorMsg(expected.size(), actual.size()), expected.size(), actual.size());
    }


}
