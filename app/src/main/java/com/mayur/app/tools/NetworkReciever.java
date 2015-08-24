package com.mayur.app.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mayur on 9/08/2015.
 */
public class NetworkReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiInfo info = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
        if(info != null) {
            String ssid = info.getSSID();
            if (ssid.equals("victoria")) {
                postData();
            }
        }
    }

    public void postData() {
        // Create a new HttpClient and Post Header
        try {
            URL url = new URL("http://wireless page");

            Map<String,Object> params = new HashMap<>();
            params.put("name", "Freddie the Fish");
            params.put("email", "fishie@seamail.example.com");
            params.put("reply_to_thread", 10394);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
