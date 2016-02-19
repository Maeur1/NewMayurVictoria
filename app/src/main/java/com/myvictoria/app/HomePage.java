package com.myvictoria.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.util.Log;
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
    protected void onResume() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //This is to make sure that the drawer updates with new username, if the user changes it
        pref.registerOnSharedPreferenceChangeListener(this);
        if (pref.getBoolean("SETUP_REQUIRED", true)) {
            if (getFragmentManager().findFragmentByTag("MAIN_FRAGMENT") == null) {
                Log.d("RESUMED", getFragmentManager().findFragmentByTag("MAIN_FRAGMENT").toString());
                fragmentManager = getFragmentManager();
                insertFragment(0);
            }
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //This is to make sure that the drawer updates with new username, if the user changes it
        pref.registerOnSharedPreferenceChangeListener(this);
        //Check if the user needs to setup the app for first time use
        if (pref.getBoolean("SETUP_REQUIRED", true)) {
            Intent i = new Intent(this, Setup.class);
            startActivity(i);
        }

        //Setup the home page
        setContentView(R.layout.activity_home_page);

        //Setup the toolbar at the top, for the Support ActionBar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //This is for the drawer setup, like creating the list of side drawer items
        mSiteTitles = getResources().getStringArray(R.array.site_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                this,
                (pref.getString("profile", "none").equals("none")) ?
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture) :
                        Uri.parse(pref.getString("profile", "none")),
                pref.getString("username", getString(R.string.no_username)),
                pref.getString("subtitle", getString(R.string.no_subtitle))));

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

        //Set the drawer to be listened to by the drawer
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //Check if we need to reload the original item that was already selected
        if (savedInstanceState == null) {
            //Start on the MyVictoria Portal
            fragmentManager = getFragmentManager();
            insertFragment(0);
        } else {
            //Otherwise start on what was already remembered
            fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, getFragmentManager().findFragmentByTag("MAIN_FRAGMENT"));
            ft.commit();
        }
    }

    private void insertFragment(int position) {
        // update the main content by replacing fragments, I know this is really crappy
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        String website = getResources().getStringArray(R.array.websites)[position];
        if (!website.isEmpty()) {
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

    @Override
    public void onClick(View view, int position) {
        if (position == 0) {
            //They have tapped the top profile picture
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
            //They have tapped a item from the drawer
            insertFragment(position - 1);
        }
    }

    private void openCamera() {
        //This stuff was pretty much copy paste from stackoverflow, but forgot where from

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, 2);
        }
    }

    private void deleteProfile() {
        //If people dont like selfies, they go to this method

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().remove("profile").apply();
        mDrawerList.setAdapter(new SiteAdapter(mSiteTitles,
                this,
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture),
                pref.getString("username", getString(R.string.no_username)),
                pref.getString("subtitle", getString(R.string.no_subtitle))));
    }

    private void openGallery() {
        //Let the user chose a selfie from their internal storage
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/jpeg");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Handle getting the photo back from the external application

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
        } else if (requestCode == 2) {
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
                (pref.getString("profile", "none").equals("none")) ?
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_contact_picture) :
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
