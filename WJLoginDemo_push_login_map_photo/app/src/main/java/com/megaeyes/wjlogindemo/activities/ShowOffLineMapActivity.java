package com.megaeyes.wjlogindemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by DaiBin on 2016-03-20.
 */
public class ShowOffLineMapActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String LTAG = ShowOffLineMapActivity.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // 介绍如何使用个性化地图，需在MapView 构造前设置个性化地图文件路径
        // 注: 设置个性化地图，将会影响所有地图实例。
        // MapView.setCustomMapStylePath("个性化地图config绝对路径");
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
        } else {
            mMapView = new MapView(this, new BaiduMapOptions());
        }
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }
}
