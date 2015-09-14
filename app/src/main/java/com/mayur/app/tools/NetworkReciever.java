package com.mayur.app.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mayur.app.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Mayur on 9/08/2015.
 */
public class NetworkReciever extends BroadcastReceiver {

    SharedPreferences prefs;
    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        WifiInfo info = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        Log.d("INFO", "Incoming Stuff");
        if(info != null) {
            Log.d("INFO RECIEVED", "Wifi Info Detected");
            Log.d("INFO RECEIVED", info.getSSID());
            String ssid = info.getSSID();
            if (ssid.equals("\"victoria\"")) {
                Log.d("INFO", "Victoria Wifi Detected");
                AutoLoginTask a = new AutoLoginTask();
                a.execute();
            }
        }
    }

    class AutoLoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread t = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        super.run();
                    }
                };
                t.run();
                t.join();
                URL url = new URL("https://wireless.victoria.ac.nz/login.html");

                StringBuilder postData = new StringBuilder();
                postData.append("buttonClicked=4&err_flag=0&err_msg=&info_flag=0&info_msg=&redirect_url=" +
                        "&network_name=Guest+Network&username=Student%5C");
                postData.append(prefs.getString("username", ""));
                postData.append("&password=");
                postData.append(prefs.getString("password", ""));

                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);
                InputStream is = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String result = sb.toString();
                Log.d("RETURNED URL", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
