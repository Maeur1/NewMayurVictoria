package com.mayur.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePage extends AppCompatActivity implements SiteAdapter.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    FragmentManager fragmentManager;
    private String[] mSiteTitles;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment webview = getFragmentManager().findFragmentById(R.id.content_frame);
        if (webview instanceof InternetFragment) {
            ((InternetFragment) webview).internet.reload();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Fragment webview = getFragmentManager().findFragmentById(R.id.content_frame);
        if (webview instanceof InternetFragment) {
            if (((InternetFragment) webview).close()) {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mSiteTitles = getResources().getStringArray(R.array.site_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                this,
                (pref.getString("profile", "none").equals("none"))?
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture):
                Uri.parse(pref.getString("profile", "none")),
                pref.getString("username", getString(R.string.no_username)),
                pref.getString("subtitle", getString(R.string.no_subtitle))));
        // enable ActionBar app icon to behave as action to toggle nav drawer

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            fragmentManager = getFragmentManager();
            selectItem(0);
        } else {
            fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, getFragmentManager().findFragmentByTag("MAIN_FRAGMENT"));
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        String website = getResources().getStringArray(R.array.websites)[position];
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (!hasInternet() && info.getSSID().contains("victoria")) {
            ft.replace(R.id.content_frame, InternetFragment.newInstance("https://wireless.victoria.ac.nz/fs/customwebauth/login.html"), "MAIN_FRAGMENT");
        } else if (!website.isEmpty()) {
            ft.replace(R.id.content_frame, InternetFragment.newInstance(website), "MAIN_FRAGMENT");
        } else {
            switch (position) {
                case 1:
                    ft.replace(R.id.content_frame, new MapFragment(), "MAIN_FRAGMENT");
                    break;
                case 7:
                    ft.replace(R.id.content_frame, new LectureFragment(), "MAIN_FRAGMENT");
                    break;
                case 9:
                    ft.replace(R.id.content_frame, new SettingsFragment(), "MAIN_FRAGMENT");
                    break;
            }
        }
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mSiteTitles[position + 1]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private boolean hasInternet() {
        boolean haveConnectedWifi = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        cm.setNetworkPreference(ConnectivityManager.TYPE_WIFI);
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                haveConnectedWifi = false;
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                if (ni.isConnected() && activeNetworkInfo != null) {
                    haveConnectedWifi = true;
                }
            }
            if(ni.getTypeName().equalsIgnoreCase("MOBILE")){
                haveConnectedWifi = true;
            }
        }
        return haveConnectedWifi;
    }

    @Override
    public void onClick(View view, int position) {
        if (position == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.profile_dialog_title));
            builder.setItems(R.array.profile_dialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        openGallery();
                    } else if (which == 2) {
                        deleteProfile();
                    } else {
                        openCamera();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        } else {
            selectItem(position - 1);
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, 2);
        }
    }

    private void deleteProfile() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().remove("profile").apply();
        mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                this,
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture),
                pref.getString("username", getString(R.string.no_username)),
                pref.getString("subtitle", getString(R.string.no_subtitle))));
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/jpeg");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                pref.edit().putString("profile", selectedImage.toString()).apply();
                mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                        this,
                        selectedImage,
                        pref.getString("username", getString(R.string.no_username)),
                        pref.getString("subtitle", getString(R.string.no_subtitle))));
            }
        } else if(requestCode == 2){
            Uri selectedImage = Uri.fromFile(new File(mCurrentPhotoPath));

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            pref.edit().putString("profile", selectedImage.toString()).apply();
            mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                    this,
                    selectedImage,
                    pref.getString("username", getString(R.string.no_username)),
                    pref.getString("subtitle", getString(R.string.no_subtitle))));
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                this,
                (pref.getString("profile", "none").equals("none"))?
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture):
                        Uri.parse(pref.getString("profile", "none")),
                pref.getString("username", getString(R.string.no_username)),
                pref.getString("subtitle", getString(R.string.no_subtitle))));
        // enable ActionBar app icon to behave as action to toggle nav drawer

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }
}
