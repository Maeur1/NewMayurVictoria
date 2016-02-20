package com.myvictoria.app;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("This is an alert");
            builder.setTitle("About");
            builder.create();
            builder.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
