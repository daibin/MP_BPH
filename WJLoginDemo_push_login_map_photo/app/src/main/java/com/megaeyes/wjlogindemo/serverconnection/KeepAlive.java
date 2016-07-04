package com.megaeyes.wjlogindemo.serverconnection;

import android.content.SharedPreferences;
import android.util.Log;

import com.megaeyes.wjlogindemo.tools.StreamTools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by John on 2016/3/21.
 */
public class KeepAlive extends Thread {

    private String urlStr;
    private SharedPreferences preferences;
    public KeepAlive(String urlStr,SharedPreferences preferences){
        this.urlStr=urlStr;
        this.preferences=preferences;
    }

    private static final String TAG ="KeepAlive" ;

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr.toString()).openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", preferences.getString("cookie_JSESSIONID219.138.66.104",""));
            Log.d(TAG, "run: "+"Cookie:"+preferences.getString("cookie_JSESSIONID219.138.66.104","")+"----code:"+connection.getResponseCode());
            if (200 == connection.getResponseCode()) {
                Log.d(TAG, "run: keep alive!");
                InputStream is=connection.getInputStream();
                String result = StreamTools.readFromStream(is);
                Log.d(TAG, "run result: "+result);
            } else {
                Log.d(TAG, "run: failed!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
