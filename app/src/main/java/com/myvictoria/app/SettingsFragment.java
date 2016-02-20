package com.myvictoria.app;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myvictoria.app.tools.NewAlertDialog;

/**
 * Created by Mayur on 3/05/2014.
 */
public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.myvictoria.app.R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference.getKey().equals("info")){
            AlertDialog dialog = NewAlertDialog.create(getActivity());
            dialog.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}

