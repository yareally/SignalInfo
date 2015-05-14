package com.cc.signalinfo.activities

import java.util.Calendar
import android.content.Intent
import android.content.pm.PackageManager
import android.os.{Build, Bundle}
import android.preference.PreferenceActivity
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBar
import android.util.{Log}
import android.view._
import android.widget.{TextView}
import com.cc.signalinfo.{R}
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.config.AppSetup.enableStrictMode
import com.cc.signalinfo.fragments.SettingsFragment
import com.cc.signalinfo.util.PimpMyAndroid.{PimpMyActivity, PimpMyTextView}
import com.google.android.gms.ads.{InterstitialAd, AdListener, AdRequest, AdView}

/**
 * @author Wes Lanning
 * @version 2013-09-05
 */
object BaseActivity {
  final val TAG: String = BaseActivity.getClass.getSimpleName
}

class BaseActivity extends ActionBarActivity {
  import BaseActivity.TAG
  protected var actionBar: ActionBar = null
  protected var actionBarHeight: Float = 0f

  /**
   * Initialize the app.
   *
   * @param savedInstanceState - umm... the saved instance state
   */
  protected def onCreateApp(savedInstanceState: Bundle) {
    //getWindow.requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
    super.onCreate(savedInstanceState)
    enableStrictMode()
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
    actionBar = getSupportActionBar
    actionBar.setHomeButtonEnabled(true)
//    val styledAttrs: TypedArray = getTheme.obtainStyledAttributes(Array[Int](android.support.v7.appcompat.R.attr.actionBarSize))
//    actionBarHeight = styledAttrs.getDimension(0, 0)
//    styledAttrs.recycle()


    if (!AppSetup.DEBUG_BUILD) {
      val ad: AdView = this.find[AdView](R.id.adView)
/*      val iad = new InterstitialAd(this)
      iad.setAdListener(new AdListener {
        override def onAdLoaded() {
          super.onAdLoaded()
        }
      })*/
      ad.loadAd(new AdRequest.Builder().build())
    } else {
      this.find[AdView](R.id.adView).setVisibility(View.GONE)
    }
    formatFooter()
  }

  /**
   * Called to populate the ActionBar.
   *
   * @param menu - the action bar menu
   * @return true on created
   */
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.options, menu)
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

      this.find[TextView](R.id.copyright)
      .text(String.format(
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

