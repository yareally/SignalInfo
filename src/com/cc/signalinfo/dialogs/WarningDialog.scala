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
package com.cc.signalinfo.dialogs

import android.app.{AlertDialog, Dialog}
import android.content.{Context, DialogInterface, SharedPreferences}
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.{CheckBox, CompoundButton}
import com.cc.signalinfo.R
import com.cc.signalinfo.config.AppSetup
import com.cc.signalinfo.dialogs.interfaces.CallBack
import com.cc.signalinfo.util.PimpMyAndroid.PimpMyView
import com.cc.signalinfo.util.{AppHelpers}



/**
 * @author Wes Lanning
 * @version 2012-12-21
 */
class WarningDialog(callBack: CallBack)
  extends DialogFragment
          with DialogInterface.OnShowListener
          with DialogInterface.OnClickListener
          with CompoundButton.OnCheckedChangeListener {
  private var form: View = null

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    super.onCreateDialog(savedInstanceState)
    form = getActivity.getLayoutInflater.inflate(R.layout.warning_dialog, null)
    val builder: AlertDialog.Builder = new AlertDialog.Builder(getActivity)
    form.find[CheckBox](R.id.dialogNoPrompt).setOnCheckedChangeListener(this)

    val ad: AlertDialog = builder.setTitle(R.string.warningDialogTitle)
                          .setView(form)
                          .setPositiveButton(android.R.string.ok, this)
                          .setNegativeButton(android.R.string.cancel, null)
                          .create

    ad.show()
    ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false)
    ad
  }

  def onClick(dialogInterface: DialogInterface, i: Int) {
    callBack.setState(AppHelpers.userConsent(getActivity.getPreferences(Context.MODE_PRIVATE)))
  }

  def onShow(dialog: DialogInterface) {
    getDialog.asInstanceOf[AlertDialog].getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false)
  }

  override def onDismiss(unused: DialogInterface) {
    super.onDismiss(unused)
  }

  override def onCancel(unused: DialogInterface) {
    super.onCancel(unused)
  }

  def onCheckedChanged(compoundButton: CompoundButton, checkState: Boolean) {
    getDialog.asInstanceOf[AlertDialog]
    .getButton(DialogInterface.BUTTON_POSITIVE)
    .setEnabled(checkState)

    val settings: SharedPreferences = getActivity.getPreferences(Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = settings.edit
    editor.putBoolean(AppSetup.PROMPT_SETTING, checkState).commit
  }


}

