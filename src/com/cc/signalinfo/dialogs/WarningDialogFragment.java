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

package com.cc.signalinfo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.cc.signalinfo.R;
import com.cc.signalinfo.config.AppSetup;
import com.cc.signalinfo.util.SignalHelpers;

/**
 * @author Wes Lanning
 * @version 2012-12-21
 */
public class WarningDialogFragment extends DialogFragment
    implements DialogInterface.OnShowListener,
    DialogInterface.OnClickListener,
    CompoundButton.OnCheckedChangeListener
{
    private View form = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        form = getActivity().getLayoutInflater().inflate(R.layout.warning_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ((CheckBox) form.findViewById(R.id.dialogNoPrompt)).setOnCheckedChangeListener(this);

        builder
            .setTitle(R.string.warningDialogTitle).setView(form)
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, null)
            .create();

        AlertDialog ad = builder.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        return ad;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i)
    {
        if (SignalHelpers.userConsent(getActivity().getPreferences(Context.MODE_PRIVATE))) {
            startActivity(SignalHelpers.getAdditionalSettings());
        }
    }

    @Override
    public void onShow(DialogInterface dialog)
    {
        ((AlertDialog) this.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onDismiss(DialogInterface unused)
    {
        super.onDismiss(unused);
    }

    @Override
    public void onCancel(DialogInterface unused)
    {
        super.onCancel(unused);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checkState)
    {
        ((AlertDialog) this.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(checkState);
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(AppSetup.PROMPT_SETTING, checkState).commit();
    }
}
