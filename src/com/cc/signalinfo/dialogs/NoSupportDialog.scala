/*
 *
 * Copyright (c) 2015 Wes Lanning, http://codingcreation.com
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
package com.cc.signalinfo.dialogs

import android.app.{AlertDialog, Dialog}
import android.content.{Context, DialogInterface, SharedPreferences}
import android.os.{CountDownTimer, Bundle}
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.{CheckBox, CompoundButton, Toast}
import com.cc.signalinfo.R
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.util.{AppHelpers, TerminalCommands}
import com.cc.signalinfo.util.PimpMyAndroid.PimpMyTextView

/**
 * @author Wes Lanning
 * @version 2012-12-21
 */
class NoSupportDialog
  extends DialogFragment
          with DialogInterface.OnShowListener
          with DialogInterface.OnClickListener {
  private var form: View = null

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    super.onCreateDialog(savedInstanceState)
    form = getActivity.getLayoutInflater.inflate(R.layout.no_support_dialog, null)
    val builder: AlertDialog.Builder = new AlertDialog.Builder(getActivity)

    builder.setTitle(R.string.noSupportDialogTitle)
    .setView(form)
    .setPositiveButton(android.R.string.ok, this)
    .create

    val ad: AlertDialog = builder.show
    initDialog(ad)
    ad
  }

  def onClick(dialogInterface: DialogInterface, i: Int) {

  }

  def onShow(dialog: DialogInterface) {
    //val ad = this.getDialog.asInstanceOf[AlertDialog]
  //  initDialog(ad)
  }

  override def onDismiss(unused: DialogInterface) {
    super.onDismiss(unused)
  }

  override def onCancel(unused: DialogInterface) {
    super.onCancel(unused)
  }

  private def initDialog(ad: AlertDialog) {
    ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false)

    new CountDownTimer(5000, 1000) {
      override def onTick(millisUntilFinished: Long) = {
        // The tl;dr Samsung device owners crowd end up crashing here.
        // I didn't realize why since the mobile users that it happens on can't ever bother to email me (shocker).
        // However, I eventually managed to replicate it by pretending my device is a locked down sucky Samsung device
        // by throwing exceptions to reach the "Your device sucks and is locked down dialog"
        // and then pretending I'm a super impatient user who doesn't like to wait 5 seconds and read some useful info.
        // By hitting the back button repeatedly while the dialog is counting down
        // it causes the dialog to come detached from the activity and crashes the entire app
        // for a few users with short attention spans

        // Checking if the activity is still attached to the dialog prevents crashes from users going
        // "OMG TOO MUCH SHIT TO READ ABOUT WHY I CAN'T ACCESS THIS SHORCUT...FUCK THIS" (mashing back button out of the dialog)
        val okTxt = if (getActivity == null) "OK" else getActivity.getString(android.R.string.ok)
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setText(s"$okTxt (${millisUntilFinished / 1000 })")
      }

      override def onFinish() {
        ad.getButton(DialogInterface.BUTTON_POSITIVE).text(android.R.string.ok).setEnabled(true)
      }
    }.start()
  }
}

