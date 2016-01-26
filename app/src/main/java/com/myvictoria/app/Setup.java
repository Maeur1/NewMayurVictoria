package com.myvictoria.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Mayur on 24/01/2016.
 */
public class Setup extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private int screen_number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        screen_number = 1;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.setup_content, SetupFragment.newInstance(screen_number), "SETUP_FRAGMENT");
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(screen_number == 1){
            String user = ((EditText)findViewById(R.id.username)).getText().toString();
            String pass = ((EditText)findViewById(R.id.password)).getText().toString();
            if(user.isEmpty() || pass.isEmpty()){
                Toast.makeText(Setup.this, "Username or Pass Not Set", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("username", user).apply();
            prefs.edit().putString("password", pass).apply();
        }

        if(screen_number < 2){
            screen_number++;
            swap_setupFragment(screen_number);
        } else {
            prefs.edit().putBoolean("SETUP_REQUIRED", false).apply();
            finish();
        }
    }

    private void swap_setupFragment(int screen_number) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.setup_content, SetupFragment.newInstance(screen_number), "SETUP_FRAGMENT");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}
