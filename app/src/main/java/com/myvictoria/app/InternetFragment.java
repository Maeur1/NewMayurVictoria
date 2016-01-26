package com.myvictoria.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class InternetFragment extends Fragment {

    private String cookie;

    private static String ARG_URL = "https://my.vuw.ac.nz/cp/home/displaylogin";
    public WebView internet;
    private ProgressBar prog;
    public String orginUrl;
    private boolean found;
    private SharedPreferences pref;
    private int backCounter;
    private boolean isSafe = true;

    @Override
    public void onStop() {
        if(!isSafe){
            internet.stopLoading();
        }
        super.onStop();
    }


    public boolean close(){
        if(internet.getUrl().contains("wireless")){
            return true;
        } else if(backCounter > 0) {
            backCounter -= 3;
            internet.goBack();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setArguments(Bundle args) {
        ARG_URL = args.getString("URL");
        super.setArguments(args);
    }

    public static InternetFragment newInstance(String url) {
        InternetFragment fragment = new InternetFragment();
        Bundle b = new Bundle();
        b.putString("URL", url);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        internet.saveState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(com.myvictoria.app.R.layout.fragment_internet, container, false);
        prog = (ProgressBar) view.findViewById(com.myvictoria.app.R.id.progressBar);
        internet = (WebView) view.findViewById(com.myvictoria.app.R.id.webview);
        WebSettings ws = internet.getSettings();
        ws.setLoadsImagesAutomatically(true);
        ws.setJavaScriptEnabled(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);
        ws.setDomStorageEnabled(true);
        internet.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        internet.setWebViewClient(new MyBrowser());
        internet.setInitialScale(100);
        internet.setWebChromeClient(new MyChrome());
        if(savedInstanceState != null){
            internet.restoreState(savedInstanceState);
        } else {
            internet.loadUrl(ARG_URL);
        }
        return view;
    }

    private class MyChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            isSafe = false;
            prog.setVisibility(View.VISIBLE);
            prog.setProgress(newProgress);
            if (newProgress == 100) {
                prog.setProgress(0);
                prog.setVisibility(View.GONE);
                isSafe = true;
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.endsWith(".pdf") || url.endsWith(".pptx") || url.endsWith(".ppt")) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf('/')+1, url.length()));
                DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("User-Agent", view.getSettings().getUserAgentString());
                manager.enqueue(request);
            } else {
                backCounter++;
                view.loadUrl(url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            cookie = android.webkit.CookieManager.getInstance().getCookie(url);
            Activity ac = getActivity();
            if(ac != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(ac);
                if (url.equals("https://my.vuw.ac.nz/cp/home/displaylogin")) {
                    view.loadUrl("javascript:document.getElementById('pass').value = '"
                            + pref.getString("password", getString(com.myvictoria.app.R.string.no_password))
                            + "';document.getElementById('user').value= '"
                            + pref.getString("username", getString(com.myvictoria.app.R.string.no_username))
                            + "';login();");
                } else if (url.equals("https://blackboard.vuw.ac.nz/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_1_1")) {
                    view.loadUrl("javascript:document.getElementById('password').value = '"
                            + pref.getString("password", getString(com.myvictoria.app.R.string.no_password))
                            + "';document.getElementById('user_id').value = '"
                            + pref.getString("username", getString(com.myvictoria.app.R.string.no_username))
                            + "';document.getElementsByClassName('submit button-1')[0].click();");
                } else if (url.contains("https://student-sa.victoria.ac.nz/Authentication/Login.aspx?ReturnUrl=%2fStudent.asp")) {
                    view.loadUrl("javascript:document.getElementsByName('ctl00$MainContent$password')[0].value = '"
                            + pref.getString("password", getString(com.myvictoria.app.R.string.no_password))
                            + "';document.getElementsByName('ctl00$MainContent$userName')[0].value = '"
                            + pref.getString("username", getString(com.myvictoria.app.R.string.no_username))
                            + "';document.getElementById('ctl00_MainContent_doLogin').click();");
                } else if (url.equals("https://library.victoria.ac.nz/roombooking/edit_entry.php")) {
                    view.loadUrl("javascript:if(document.getElementById('NewUserName')!=null){document.getElementsByName('NewUserPassword')[0].value = '"
                            + pref.getString("password", getString(com.myvictoria.app.R.string.no_password))
                            + "';document.getElementsByName('NewUserName')[0].value = '"
                            + pref.getString("username", getString(com.myvictoria.app.R.string.no_username))
                            + "';document.getElementsByClassName('submit')[0].click();" +
                            "}");
                } else if (url.contains("wireless")) {
                    Log.d("WIRELESS ADDRESS", view.getUrl());
                    view.loadUrl("javascript:document.getElementById('password').value = '"
                            + pref.getString("password", getString(com.myvictoria.app.R.string.no_password))
                            + "';document.getElementById('username').value = '"
                            + pref.getString("username", getString(com.myvictoria.app.R.string.no_username))
                            + "';document.getElementsByClassName('button')[0].click();");
                } else if (!found) {
                    orginUrl = url;
                    found = true;
                }
            }
        }
    }

}
