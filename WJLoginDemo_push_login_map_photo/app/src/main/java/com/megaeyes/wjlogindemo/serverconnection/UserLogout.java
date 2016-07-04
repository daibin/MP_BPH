package com.megaeyes.wjlogindemo.serverconnection;

import android.content.SharedPreferences;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by John on 2016/4/13.
 */
public class UserLogout extends Thread{
    private static final String TAG = "UserLogout";
    private static final String BASE_URL="http://219.138.66.104:8080/BPHCenter/";
    private SharedPreferences preferences;
    public UserLogout(SharedPreferences preferences){
        this.preferences=preferences;
    }
    @Override
    public void run() {
        try{
            String urlstring = BASE_URL+"appController/logout.do?policeNumber=" + preferences.getString("number", "");
            Log.i(TAG,urlstring);
            URL url = new URL(urlstring);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", preferences.getString("Cookie",""));
            Log.i(TAG,connection.getResponseCode()+"");
            if (200 == connection.getResponseCode()) {
                System.out.println("Logout Success!");
            } else {
                System.out.println("failed!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
