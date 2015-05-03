package com.cc.signalinfo.util

import android.app.Activity
import android.content.{SharedPreferences, ComponentName, Intent}
import android.support.v4.app.FragmentActivity
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.dialogs.NoSupportDialog


import scala.concurrent.Await
import scala.concurrent.duration._
/**
 * @author Wes Lanning
 * @version 2013-11-24
 */
object AppHelpers
{
    private final val TAG = AppHelpers.getClass.getSimpleName

    /**
     * Get the intent that launches the additional radio settings screen
     *
     * @return the intent for the settings area
     *
     * @throws SecurityException the security exception
     */
    def getAdditionalSettings: Intent =
    {
        val intent = new Intent(Intent.ACTION_VIEW)
        val showSettings = new ComponentName("com.android.settings", "com.android.settings.TestingSettings")
        intent.setComponent(showSettings)
    }

    /**
     * Prompts the user with a warning and makes them agree by pressing a
     * checkbox before they can continue onto the next screen.
     *
     * @param settings - used to verify if they previously agreed
     * @return true if user consents to the warning screen
     */
    def userConsent(settings: SharedPreferences): Boolean =
    {
        settings.contains(AppSetup.PROMPT_SETTING) && settings.getBoolean(AppSetup.PROMPT_SETTING, false)
    }

    /**
     * Has lte api.
     *
     * @param settings the settings
     * @return the boolean
     */
    def hasLteApi(settings: SharedPreferences): Boolean =
    {
        settings.contains(AppSetup.OLD_FUCKING_DEVICE) && settings.getBoolean(AppSetup.OLD_FUCKING_DEVICE, false)
    }

    def launchTestingSettings(activity: FragmentActivity) {
        try {
            activity.startActivity(AppHelpers.getAdditionalSettings)
        }
        catch {
            case ignored: Any ⇒ try {
                val result = Await.result(TerminalCommands.launchActivity("com.android.settings", "TestingSettings"), 10.seconds)

                if (result != 0) new NoSupportDialog().show(activity.getSupportFragmentManager, "Sorry")
            }
            catch {
                case ignored: Any ⇒ new NoSupportDialog().show(activity.getSupportFragmentManager, "Sorry")
            }
        }
    }
}
