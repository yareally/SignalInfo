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

package com.android.signalinfo;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.signalinfo.R.id;
import com.android.signalinfo.R.layout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Make sure to add "android.permission.CHANGE_NETWORK_STATE"
 * to the manifest to use this or crashy you will go.
 *
 * @author Wes Lanning
 * @version 1.0
 */
public class SignalInfo extends Activity implements OnClickListener
{
    private static final String TAG = "Signal";
    private MyPhoneStateListener listen;
    private TelephonyManager     tm;
    private static final String SD_DIR    = "/Android/data/signalinfo";
    private static final String SS_SUFFIX = "signal-state.png";
    private Button screenShotBtn;

    private static final int GSM_SIG_STRENGTH = 1;
    private static final int GSM_BIT_ERROR    = 2;
    private static final int CDMA_SIGNAL      = 3;
    private static final int CDMA_ECIO        = 4;
    private static final int EVDO_SIGNAL      = 5;
    private static final int EVDO_ECIO        = 6;
    private static final int EVDO_SNR         = 7;
    private static final int LTE_SIG_STRENGTH = 8;
    private static final int LTE_RSRP         = 9;
    private static final int LTE_RSRQ         = 10;
    private static final int LTE_SNR          = 11;
    private static final int LTE_CQI          = 12;

    /**
     * Initialize the app.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.main);


        listen = new MyPhoneStateListener();
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        tm.listen(listen, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
/*        this.screenShotBtn = (Button) this.findViewById(id.saveScreenShot);
        this.screenShotBtn.setOnClickListener(this);*/

        this.setPhoneInfo();

    }





    /**
     * Set the phone model, OS version, carrier name on the screen
     */
    private void setPhoneInfo()
    {
        TextView t = (TextView) findViewById(id.phoneName);
        t.setText(Build.MANUFACTURER + ' ' + Build.MODEL);

        t = (TextView) findViewById(id.phoneModel);
        t.setText(Build.PRODUCT + '/' + Build.DEVICE + " (" + Build.ID + ") ");

        t = (TextView) findViewById(id.androidVersion);
        t.setText(Build.VERSION.RELEASE + " (API version " + Build.VERSION.SDK_INT + ')');

        t = (TextView) findViewById(id.carrierName);
        t.setText(tm.getNetworkOperatorName());

        t = (TextView) findViewById(id.buildHost);
        t.setText(Build.HOST);
    }

    /**
     * Stop recording when screen is not in the front.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        tm.listen(listen, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * Start recording when the screen is on again.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        tm.listen(listen, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * Handle button pushes
     *
     * @param view - the screen where the button was pushed.
     */
    public void onClick(View view)
    {
        if (this.screenShotBtn.isPressed()) {
            String filename = this.getScreen(getWindow().getDecorView().findViewById(R.id.content));

            if ("".equals(filename)) {
                Toast.makeText(this.getApplicationContext(), "Could not save screenshot", 3);
            }
            else {
                Toast.makeText(this.getApplicationContext(), "Screenshot saved to " + filename, 3);
            }
        }
    }

    /**
     * Captures a screen shot.
     *
     * @return true if screen shot was taken and saved. False otherwise.
     */
    private String getScreen(View content)
    {
        content.setDrawingCacheEnabled(true);
        content.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        content.layout(0, 0, content.getMeasuredWidth(), content.getMeasuredHeight());
        content.buildDrawingCache(true);

        Bitmap bitmap = content.getDrawingCache();

        Log.d(TAG, "Creating new directory at " + Environment.getExternalStorageDirectory()
            + SD_DIR);

        File file = new File(Environment.getExternalStorageDirectory()
            + SD_DIR);

        if (!file.mkdirs()) {
            Log.e(TAG, "Could not make directory " +
                Environment.getExternalStorageDirectory() + SD_DIR + '/' + SS_SUFFIX);
        }


        file = new File(Environment.getExternalStorageDirectory() + "/"
            + SD_DIR + '/' + System.currentTimeMillis() / 1000 + '-' + SS_SUFFIX);

        Log.d(TAG, "Creating new file at " + file.getAbsolutePath());

        FileOutputStream ostream = null;

        try {
            if (file.createNewFile()) {
                ostream = new FileOutputStream(file);

                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream)) {
                    //  content.setDrawingCacheEnabled(false);
                    return file.getAbsolutePath();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    /**
     * Private helper class to listen for network signal changes.
     */
    private class MyPhoneStateListener extends PhoneStateListener
    {


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
                setSignalInfo(signalStrength);
                Log.d(TAG, signalStrength.toString());
            }
        }
    }


    /**
     * Set the signal info the user sees.
     *
     * @param signalStrength - contains all the signal info
     * @see SignalStrength for more info.
     */
    private void setSignalInfo(SignalStrength signalStrength)
    {
        TextView t = (TextView) findViewById(id.networkType);
        t.setText(signalStrength.isGsm() ? "gsm | lte" : "cdma | evdo");
        String[] sigInfo = signalStrength.toString().split(" ");
        Log.d("Signal Array", Arrays.toString(sigInfo));

        t = (TextView) findViewById(id.cdmaSignal);
        t.setText(sigInfo[CDMA_SIGNAL] + " db");

        t = (TextView) findViewById(id.evdoSignal);
        t.setText(sigInfo[EVDO_SIGNAL] + " db");

        t = (TextView) findViewById(id.cdmaECIO);

        try {
            t.setText(sigInfo[CDMA_ECIO]);
        }
        catch (Resources.NotFoundException e) {
            t.setText("N/A");
        }

        t = (TextView) findViewById(id.evdoECIO);

        try {
            t.setText(sigInfo[EVDO_ECIO]);
        }
        catch (Resources.NotFoundException e) {
            t.setText("N/A");
        }

        t = (TextView) findViewById(id.evdoSNR);

        try {
            t.setText(sigInfo[EVDO_SNR]);
        }
        catch (Resources.NotFoundException e) {
            t.setText("N/A");
        }

        t = (TextView) findViewById(id.gsmSigStrength);
        t.setText("99".equals(sigInfo[GSM_SIG_STRENGTH]) ? "N/A" : sigInfo[GSM_SIG_STRENGTH]);

        t = (TextView) findViewById(id.gsmBitError);
        t.setText("-1".equals(sigInfo[GSM_BIT_ERROR]) ? "N/A" : sigInfo[GSM_BIT_ERROR]);

        if ("-1".equals(sigInfo[LTE_RSRP])) {
            return; // no LTE
        }

        t = (TextView) findViewById(id.rsrpSignal);
        t.setText(sigInfo[LTE_RSRP] + " db");

        t = (TextView) findViewById(id.rssiSignal);
        int rssi = -17 - Integer.parseInt(sigInfo[LTE_RSRP]) - Integer.parseInt(sigInfo[LTE_RSRQ]);
        t.setText('-' + Integer.toString(rssi) + " db");


        t = (TextView) findViewById(id.lteRSRQ);
        t.setText('-' + sigInfo[LTE_RSRQ] + " db");

        t = (TextView) findViewById(id.lteSNR);
        t.setText(sigInfo[LTE_SNR]);

        t = (TextView) findViewById(id.lteCQI);
        t.setText(sigInfo[LTE_CQI]);


        t = (TextView) findViewById(id.lteSigStrength);
        t.setText(sigInfo[LTE_SIG_STRENGTH]);
    }

}
