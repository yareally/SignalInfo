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

package com.cc.signalinfo.exceptions;

/**
 * @author Wes Lanning
 * @version 2013-05-10
 */
public class InvalidSignalDataException extends Exception
{
    private static final long serialVersionUID = 5594861351450806724L;

    /**
     * Thrown when the signal data returned from the OS is completely
     * fucked up due to one of the following reasons:
     *
     * 1) The OEM ignored Google specs for the RIL
     * 2) The ROM developer fucked up the RIL because they couldn't code
     * their way out of a paper bag.
     * 3) No signal data was reported and added to the data array (goes with #1)
     *
     * @param detailMessage the detail message for this exception.
     */
    public InvalidSignalDataException(String detailMessage)
    {
        super(detailMessage);
    }

    /**
     * Thrown when the signal data returned from the OS is completely
     * fucked up due to one of the following reasons:
     *
     * 1) The OEM ignored Google specs for the RIL
     * 2) The ROM developer fucked up the RIL because they couldn't code
     * their way out of a paper bag.
     * 3) No signal data was reported and added to the data array (goes with #1)
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable the cause of this exception.
     */
    public InvalidSignalDataException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    /**
     * Thrown when the signal data returned from the OS is completely
     * fucked up due to one of the following reasons:
     *
     * 1) The OEM ignored Google specs for the RIL
     * 2) The ROM developer fucked up the RIL because they couldn't code
     * their way out of a paper bag.
     * 3) No signal data was reported and added to the data array (goes with #1)
     *
     * @param throwable the cause of this exception.
     */
    public InvalidSignalDataException(Throwable throwable)
    {
        super(throwable);
    }
}
