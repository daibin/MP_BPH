package com.megaeyes.wjlogindemo.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megaeyes.wjlogindemo.option.LocationOption;
import com.megaeyes.wjlogindemo.tools.AsyncHttpService;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MapService extends Service {

    private static final int LOCATION = 1;
    private static final String TAG ="MapService" ;
    private SharedPreferences preferences;
    public LocationClient mLocationClient = null;
    private double longtitude, latitude;
    private String addrStr;
    private Editor editor;
    private static final String BASE_URL="http://219.138.66.104:8080/BPHCenter/";

    private AsyncHttpService asyncHttpService;

    public void onCreate() {
        asyncHttpService=AsyncHttpService.getAsyncHttpServiceInstance(getApplicationContext());
        preferences=getSharedPreferences("CookiePrefsFile", Activity.MODE_PRIVATE);
        editor=preferences.edit();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        SigninLocationListener locationListener = new SigninLocationListener();
        mLocationClient.registerLocationListener(locationListener);    //注册监听函数
        LocationOption.initLocation(mLocationClient);
        mLocationClient.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class SigninLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            longtitude = bdLocation.getLongitude();
            latitude = bdLocation.getLatitude();
            addrStr=bdLocation.getAddrStr();
            editor.putString("longtitude",longtitude+"");
            editor.putString("latitude",latitude+"");
            editor.putString("addrStr",addrStr+"");
            editor.commit();
            handler.sendEmptyMessage(0);
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            RequestParams params1 = new RequestParams();
            params1.put("longitude", longtitude-0.006521);
            params1.put("latitude", latitude-0.00718);
            params1.put("objectType", "PoliceLayer");
            params1.put("objectId", preferences.getInt("id",0));
            params1.put("userId", preferences.getInt("id",0));

            RequestParams params2 = new RequestParams();
            params2.put("policeNumber",preferences.getString("number",""));

            asyncHttpService.get("client/coordinate/insert.do",params1, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG, "onSuccess: GPS uploading"+response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });

            asyncHttpService.get("appController/heart.do",params2, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG, "onSuccess: keep alive"+response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    };
}
