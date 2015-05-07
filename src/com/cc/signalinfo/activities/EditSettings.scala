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

import java.util.{List ⇒ Jlist}

import android.annotation.TargetApi
import android.content.{Context, Intent}
import android.os.{Build, Bundle}
import android.preference.PreferenceActivity
import android.support.v7.widget._
import android.util.{AttributeSet, TypedValue}
import android.view.{LayoutInflater, MenuItem, View, ViewGroup}
import android.widget.{LinearLayout, ListView}
import com.cc.signalinfo.R
import com.cc.signalinfo.fragments.SettingsFragment
import com.cc.signalinfo.util.PimpMyAndroid.{PimpMyActivity, PimpMyToolbar}

/**
 * Routes to the specified preference screen based on user input
 * from the action bar. Preference screen to route to has already
 * been added to a bundle and just needs to be grabbed to decide
 * where to route to.
 *
 * @author Wes Lanning
 * @version 2013-05-09
 */
class EditSettings extends PreferenceActivity {

  override def onCreateView(name: String, context: Context, attrs: AttributeSet): View = {
    val view = super.onCreateView(name, context, attrs)
    if (view != null) return view

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      name match {
        case "EditText" ⇒ new AppCompatEditText(this, attrs)
        case "Spinner" ⇒ new AppCompatSpinner(this, attrs)
        case "CheckBox" ⇒ new AppCompatCheckBox(this, attrs)
        case "RadioButton" ⇒ new AppCompatRadioButton(this, attrs)
        case "CheckedTextView" ⇒ new AppCompatCheckedTextView(this, attrs)
        case _ ⇒
      }
    }
    null
  }

  override def onPostCreate(savedInstanceState: Bundle) {
    super.onPostCreate(savedInstanceState)
    var actionBar: Toolbar = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      val root = findViewById(android.R.id.list).getParent.getParent.getParent.asInstanceOf[LinearLayout]
      actionBar = LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false).asInstanceOf[Toolbar]
      root.addView(actionBar, 0) // insert at top
    }
    else {
      val root = this.find[ViewGroup](android.R.id.content)
      val content = root.getChildAt(0).asInstanceOf[ListView]
      root.removeAllViews()
      actionBar = LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false).asInstanceOf[Toolbar]
      val tv = new TypedValue

      val height = if (getTheme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
      }
      else {
        actionBar.getHeight
      }
      content.setPadding(0, height, 0, 0)
      root.addView(content)
      root.addView(actionBar)
    }

    actionBar.navClick((view: View) ⇒ {
      EditSettings.this.finish()

    })

    // for crappy old devices, load them without headers
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      //noinspection ScalaDeprecation
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
   * Android 4.4+ requires this
   *
   * @param fragmentName - frag to validate
   * @return
   */
  override def isValidFragment(fragmentName: String): Boolean = {
    if (classOf[SettingsFragment].getName.equals(fragmentName)) {
      return true
    }
    false
  }
}