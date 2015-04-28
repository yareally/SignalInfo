package com.cc.signalinfo.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.actionbarsherlock.app.{SherlockFragmentActivity, ActionBar}
import com.actionbarsherlock.view.{Menu, MenuItem}
import com.cc.signalinfo.BuildConfig
import com.cc.signalinfo.R
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.config.AppSetup.enableStrictMode
import com.cc.signalinfo.fragments.SettingsFragment
import com.google.android.gms.ads.{AdRequest, AdView}
import java.util.Calendar

/**
 * @author Wes Lanning
 * @version 2013-09-05
 */
object BaseActivity
{
    final val TAG: String = BaseActivity.getClass.getSimpleName
}

class BaseActivity extends SherlockFragmentActivity
{

    import BaseActivity.TAG

    protected var actionBar: ActionBar = null

    /**
     * Initialize the app.
     *
     * @param savedInstanceState - umm... the saved instance state
     */
    protected def onCreateApp(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        enableStrictMode()
        actionBar = getSupportActionBar
        actionBar.setHomeButtonEnabled(true)
    }

    /**
     * Initialize the app.
     *
     * @param layout - reference to the layout to bind to the activity
     * @param savedInstanceState - umm... the saved instance state
     */
    protected def onCreate(layout: Int, savedInstanceState: Bundle) {
        this.onCreateApp(savedInstanceState)
        setContentView(layout)
        val ad: AdView = findViewById(R.id.adView).asInstanceOf[AdView]

        if (!AppSetup.DEBUG_BUILD) {
            val adRequest = new AdRequest.Builder().build()
            ad.loadAd(adRequest)
            ad.setVisibility(View.VISIBLE)
        }
        formatFooter()
    }

    /**
     * Helper to make setting the text of a TextView slightly
     * less painful. Stupid Java boilerplate.
     *
     * @param txtViewId - reference id to the TextView
     * @param text - the text to display in the TextView
     */
    protected def setTextViewText(txtViewId: java.lang.Integer, text: CharSequence) {
        findViewById(txtViewId).asInstanceOf[TextView].setText(text)
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param menu - the action bar menu
     * @return true on created
     */
    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        getSupportMenuInflater.inflate(R.menu.options, menu)
        super.onCreateOptionsMenu(menu)
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param item - item to populate
     * @return true on create
     */
    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.preferences ⇒
                loadPrefsScreen()
                return true
            case _ ⇒
        }
        super.onOptionsItemSelected(item)
    }

    /**
     * Loads up the preferences screen in the action bar.
     */
    protected def loadPrefsScreen() {
        val intent = new Intent(this, classOf[EditSettings])
        // if using Android 3.0+ and only using one preference screen, default load it
        // instead of using the big ass selection header area for preferences.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
            && getResources.getBoolean(R.bool.suppressHeader)) {

            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true)
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, classOf[SettingsFragment].getName)

            val bundle = new Bundle()
            bundle.putString("resource", "main_prefs")
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        }
        startActivity(intent)
    }

    /**
     * Formats the page footer with in the following format:
     * ©YEAR codingcreation.com | v. x.xx
     */
    protected def formatFooter() {
        try {
            val appVersion: String = getPackageManager.getPackageInfo(getPackageName, 0).versionName
            setTextViewText(R.id.copyright,
                String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance.get(Calendar.YEAR).asInstanceOf[java.lang.Integer],
                    appVersion))

            findViewById(R.id.copyright).setContentDescription(
                String.format(
                    getString(R.string.copyrightDescription),
                    appVersion))
        } catch {
            case e: PackageManager.NameNotFoundException ⇒
                Log.wtf(TAG, getString(R.string.APP_VERS_EXCEPT_MSG))
        }
    }
}

