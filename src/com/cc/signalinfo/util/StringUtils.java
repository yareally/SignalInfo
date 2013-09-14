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

package com.cc.signalinfo.util;

import com.cc.signalinfo.config.AppSetup;

/**
 * @author Wes Lanning
 * @version 2013-04-28
 */
public final class StringUtils
{
    private StringUtils() {}

    /**
     * Like C#'s isNullOrEmpty
     *
     * @param value - value to check for null or empty
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(CharSequence value)
    {
        return value == null || isEmpty(value);
    }

    /**
     * Like C#'s isNullOrWhiteSpace
     *
     * @param value - value to check for null or whitespace
     * @return true if the string is null or just whitespace
     */
    public static boolean isNullOrWhiteSpace(CharSequence value)
    {
        if (value == null) {
            return true;
        }
        for (int index = 0; index < value.length(); ++index) {
            if (!Character.isWhitespace(value.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> if, and only if, {@link #length()} is <tt>0</tt>.
     *
     * @return <tt>true</tt> if {@link #length()} is <tt>0</tt>, otherwise
     *         <tt>false</tt>
     *
     * @since 1.6
     */
    public static boolean isEmpty(CharSequence value)
    {
        return value.length() == 0;
    }

    /**
     * Compares two strings to determine if each is not
     * null or empty as well as the same value (via equals())
     *
     * @param value - first value
     * @param value2 - second value
     * @return true if the strings are not null/empty and the same value
     */
    public static boolean safeEquals(CharSequence value, CharSequence value2)
    {
        return (!isNullOrEmpty(value) && !isNullOrEmpty(value2)) && value.equals(value2);
    }
}
