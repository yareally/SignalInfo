package com.cc.signalinfo.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cc.signalinfo.BuildConfig;
import com.cc.signalinfo.R;
import com.cc.signalinfo.fragments.SettingsFragment;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.Calendar;

import static com.cc.signalinfo.config.AppSetup.enableStrictMode;

/**
 * @author Wes Lanning
 * @version 2013-09-05
 */
public class BaseActivity extends SherlockFragmentActivity
{
    private static final String    TAG       = BaseActivity.class.getSimpleName();
    protected            ActionBar actionBar = null;

    /**
     * Initialize the app.
     *
     * @param savedInstanceState - umm... the saved instance state
     */
    protected void onCreateApp(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        enableStrictMode();
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        if (!BuildConfig.DEBUG) {
            AdView ad = (AdView) findViewById(R.id.adView);
            ad.loadAd(new AdRequest());
        }
    }

    /**
     * Initialize the app.
     *
     * @param layout - reference to the layout to bind to the activity
     * @param savedInstanceState - umm... the saved instance state
     */
    protected void onCreate(int layout, Bundle savedInstanceState)
    {
        this.onCreateApp(savedInstanceState);
        setContentView(layout);
        formatFooter();
    }

    /**
     * Helper to make setting the text of a TextView slightly
     * less painful. Stupid Java boilerplate.
     *
     * @param txtViewId - reference id to the TextView
     * @param text - the text to display in the TextView
     */
    protected void setTextViewText(int txtViewId, CharSequence text)
    {
        ((TextView) findViewById(txtViewId)).setText(text);
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param menu - the action bar menu
     * @return true on created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param item - item to populate
     * @return true on create
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.preferences:
                loadPrefsScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads up the preferences screen in the action bar.
     */
    protected void loadPrefsScreen()
    {
        Intent intent = new Intent(this, EditSettings.class);

        // if using Android 3.0+ and only using one preference screen, default load it
        // instead of using the big ass selection header area for preferences.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
            && getResources().getBoolean(R.bool.suppressHeader)) {

            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                SettingsFragment.class.getName());

            Bundle bundle = new Bundle();
            bundle.putString("resource", "main_prefs");
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
        }
        startActivity(intent);
    }

    /**
     * Formats the page footer with in the following format:
     * ©YEAR codingcreation.com | v. x.xx
     */
    protected void formatFooter()
    {
        try {
            String appVersion = getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;

            setTextViewText(R.id.copyright,
                String.format(
                    getString(R.string.copyright),
                    Calendar.getInstance().get(Calendar.YEAR),
                    appVersion));

            findViewById(R.id.copyright).setContentDescription(
                String.format(getString(R.string.copyrightDescription),
                    appVersion));
        } catch (PackageManager.NameNotFoundException ignored) {
            Log.wtf(TAG, "Could not display app version number!");
        }
    }
}
