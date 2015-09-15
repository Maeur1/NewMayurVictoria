package com.myvictoria.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Mayur on 3/05/2014.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.myvictoria.app.R.xml.preferences);
    }


}
