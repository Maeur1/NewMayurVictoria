package com.myvictoria.app.tools;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.myvictoria.app.R;

/**
 * Created by Mayur on 20/02/2016.
 */

public class NewAlertDialog {

    public static AlertDialog create(Context context){
        final TextView message = new TextView(context);

        final SpannableString s =
                new SpannableString(context.getText(R.string.dialog_message));

        Linkify.addLinks(s, Linkify.ALL);

        message.setMovementMethod(LinkMovementMethod.getInstance());

        message.setText(s);

        message.setPadding(30, 30, 30, 30);

        return new AlertDialog.Builder(context)
                .setTitle("About")
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_action_dismiss, null)
                .setView(message)
                .create();
    }


}