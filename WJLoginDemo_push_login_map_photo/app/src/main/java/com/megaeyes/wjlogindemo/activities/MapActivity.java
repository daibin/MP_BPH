package com.megaeyes.wjlogindemo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.megaeyes.wjlogindemo.R;
import com.megaeyes.wjlogindemo.listener.MyOrientationListener;
import com.megaeyes.wjlogindemo.option.LocationOption;

public class MapActivity extends AppCompatActivity {


    private static final String TAG ="MapActivity" ;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Context context;

    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;
    private String mLocationStr;
    private BitmapDescriptor bitmapDescriptor;
    private MapStatusUpdate statusUpdates;
    private MyOrientationListener myOrientationListener;
    private float currentX;
    private MyLocationConfiguration.LocationMode locationMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        this.context=this;
        sharedPreferences=getSharedPreferences("CookiePrefsFile", Activity.MODE_PRIVATE);
        initView();
        initLocation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaiduMap.setMapStatus(statusUpdates);
                myLocationListener.centerToMyLocation();
                Snackbar.make(view, "您目前所在位置："+mLocationStr, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.id_map_offline:
                Intent offlineMapIntent=new Intent(MapActivity.this,OffLineMapDownloadActivity.class);
                startActivity(offlineMapIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        statusUpdates = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(statusUpdates);

    }

    private void initLocation() {
        locationMode= LocationMode.NORMAL;
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        /*LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setScanSpan(1000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);*/
        LocationOption.initLocation(mLocationClient);
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked_s);
        myOrientationListener=new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrinentationChanged(float x) {
                // TODO Auto-generated method stub
                currentX=x;
            }
        });
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
            mLocationClient.start();
        myOrientationListener.start();
    }

/*    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        myOrientationListener.stop();
    }*/

  /*  @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }*/
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            MyLocationData data = new MyLocationData.Builder()//
                    .direction(currentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);
            MyLocationConfiguration configuration = new MyLocationConfiguration(locationMode,true,bitmapDescriptor);
            mBaiduMap.setMyLocationConfigeration(configuration);

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mLocationStr=location.getAddrStr();
            if (isFirstIn) {
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                        .newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                isFirstIn = false;
               /* Toast.makeText(context, location.getAddrStr(), Toast.LENGTH_LONG)
                        .show();*/
            }

          // UploadGPSInfo(mLongitude+"",mLatitude+"");
        }

        /*private void UploadGPSInfo(String longitude,String latitude) {
            String urlPath =getString(R.string.uploadGPSInfo)+"?";
            Map<String, String> params = new HashMap<String, String>();
            params.put("userId", sharedPreferences.getInt("id",0)+"");
            //params.put("longitude",longitude);
            //params.put("latitude", latitude);
           params.put("longitude",new Random().nextFloat()/1000+114.966731+"");
           params.put("latitude", new Random().nextFloat()/1000+30.121336+"");
            params.put("objectId", sharedPreferences.getInt("id",0)+"");
            params.put("objectType", "PoliceLayer");
            final StringBuilder urlStr = new StringBuilder(urlPath);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlStr.append(entry.getKey()).append("=");
                urlStr.append(entry.getValue());
                urlStr.append("&");
            }
            urlStr.deleteCharAt(urlStr.length() - 1);
            System.out.println("********url********:"+urlStr);
            new Thread(){
                @Override
                public void run() {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr.toString()).openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Cookie", sharedPreferences.getString("Cookie",""));
                        System.out.println("Cookie:"+sharedPreferences.getString("Cookie","")+"----code:"+connection.getResponseCode());
                        if (200 == connection.getResponseCode()) {
                            System.out.println("success!");
                            InputStream is=connection.getInputStream();
                            String result = StreamTools.readFromStream(is);
                            Log.i(TAG,"result"+result);
                        } else {
                            System.out.println("failed!");
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }*/

        public void centerToMyLocation() {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                    .newLatLng(latLng);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
        }
    }

}
