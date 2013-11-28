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
package com.cc.signalinfo.activities

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.actionbarsherlock.app.ActionBar
import com.actionbarsherlock.app.SherlockPreferenceActivity
import com.actionbarsherlock.view.MenuItem
import com.cc.signalinfo.R
import java.util.{List ⇒ Jlist, Map ⇒ Jmap, EnumMap ⇒ Emap, Collections}
import android.preference.PreferenceActivity

/**
 * Routes to the specified preference screen based on user input
 * from the action bar. Preference screen to route to has already
 * been added to a bundle and just needs to be grabbed to decide
 * where to route to.
 *
 * @author Wes Lanning
 * @version 2013-05-09
 */
class EditSettings extends SherlockPreferenceActivity
{
    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar = getSupportActionBar
        actionBar.setDisplayHomeAsUpEnabled(true)

        // for crappy old devices, load them without headers
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.main_prefs)
        }

    }

    /**
     * For devices Android 3.0+, load the preference header layout
     *
     * @param target - the headers to load
     */
    @TargetApi(11)
    override def onBuildHeaders(target: Jlist[PreferenceActivity.Header]) {
        loadHeadersFromResource(R.xml.preference_headers, target)
    }

    /**
     * Called to populate the ActionBar.
     *
     * @param item - item to populate
     * @return true on create
     */
    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case android.R.id.home =>
                val intent: Intent = new Intent(this, classOf[MainActivity])
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                return true
        }
        super.onOptionsItemSelected(item)
    }
}