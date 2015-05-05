package com.cc.signalinfo.util

/**
 * @author Wes Lanning
 * @version 2013-11-24
 */
object StringUtils
{
    /**
     * Returns <tt>true</tt> if, and only if, `link #length()` is <tt>0</tt>.
     *
     * @return <tt>true</tt> if { @link #length()} is <tt>0</tt>, otherwise
     *         <tt>false</tt>
     *
     * @since 1.6
     */
    def isEmpty(value: CharSequence): Boolean =
    {
        value.length == 0
    }

    /**
     * Like C#'s isNullOrEmpty
     *
     * @param value - value to check for null or empty
     * @return true if null or empty
     */
    def isNullOrEmpty(value: CharSequence): Boolean =
    {
        value == null || isEmpty(value)
    }

    /**
     * Like C#'s isNullOrWhiteSpace
     *
     * @param value - value to check for null or whitespace
     * @return true if the string is null or just whitespace
     */
    def isNullOrWhiteSpace(value: CharSequence): Boolean =
    {
        if (value == null) {
            return true
        }

        for (index ← 0 until value.length) {
            if (!Character.isWhitespace(value.charAt(index))) {
                return false
            }
        }
        true
    }


    /**
     * Compares two strings to determine if each is not
     * null or empty as well as the same value (via equals())
     *
     * @param value - first value
     * @param value2 - second value
     * @return true if the strings are not null/empty and the same value
     */
    def safeEquals(value: CharSequence, value2: CharSequence): Boolean =
    {
        !isNullOrEmpty(value) && !isNullOrEmpty(value2) && value == value2
    }
}