package com.megaeyes.wjlogindemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messagepush.client.NotificationHistoryActivity;
import com.example.messagepush.client.ServiceManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megaeyes.wjlogindemo.R;
import com.megaeyes.wjlogindemo.adapter.MyAdapter;
import com.megaeyes.wjlogindemo.bean.ItemBean;
import com.megaeyes.wjlogindemo.tools.AsyncHttpService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="MainActivity" ;
    private Button btn_sign_in;
    private static boolean isExit = false;
    private SharedPreferences preferences;
    private Editor editor;
    private GridView gridView;
    private List<ItemBean> datas;
    private MyAdapter myAdapter;
    private AsyncHttpService asyncHttpService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncHttpService=AsyncHttpService.getAsyncHttpServiceInstance(getApplicationContext());
        preferences=getSharedPreferences("CookiePrefsFile", Activity.MODE_PRIVATE);
        editor=preferences.edit();

        initDatas();
        initView();
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new SignIn(preferences).start();
                RequestParams params1=new RequestParams();
                params1.put("policeNumber",preferences.getString("number", ""));
                params1.put("longitude",preferences.getString("longtitude", ""));
                params1.put("latitude",preferences.getString("latitude", ""));
                params1.put("signType","1");
                params1.put("position",preferences.getString("addrStr", ""));
                RequestParams params2=new RequestParams();
                params2.put("policeNumber",preferences.getString("number", ""));
                params2.put("longitude",preferences.getString("longtitude", ""));
                params2.put("latitude",preferences.getString("latitude", ""));
                params2.put("signType","2");
                params2.put("position",preferences.getString("addrStr", ""));

                if(preferences.getBoolean("isOnWork",false)){
                    asyncHttpService.get("appController/signInOrOut.do",params2,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d(TAG, "onSuccess: 签出成功"+response.toString());
                            btn_sign_in.setBackground(getDrawable(R.drawable.plugin_camera_send_pressed));
                            btn_sign_in.setText("签到");
                            editor.putBoolean("isOnWork",false);
                            editor.apply();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d(TAG, "onFailure: "+errorResponse.toString());
                        }
                    });
                }else{
                    asyncHttpService.get("appController/signInOrOut.do",params1,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d(TAG, "onSuccess: 签到成功"+response.toString());
                            btn_sign_in.setBackground(getDrawable(R.drawable.plugin_camera_send_unselected));
                            btn_sign_in.setText("签出");
                            editor.putBoolean("isOnWork",true);
                            editor.apply();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d(TAG, "onFailure: "+errorResponse.toString());
                        }
                    });
                }

                TextView tv_username= (TextView) findViewById(R.id.tv_username);
                tv_username.setText(preferences.getString("name","")+",您已签到");
                TextView tv_policeId= (TextView) findViewById(R.id.tv_policeId);
                tv_policeId.setText("警号："+preferences.getString("username",""));

                ServiceManager serviceManager=new ServiceManager(MainActivity.this);
                serviceManager.setNotificationIcon(R.drawable.notification);
                serviceManager.startService();
                serviceManager.setAlias("daibin");
                List<String> tagsList=new ArrayList<String>();
                tagsList.add("sports");
                tagsList.add("music");
                serviceManager.setTags(tagsList);
            }
        });
    }

    private void initDatas() {

        datas =new ArrayList<ItemBean>();
        ItemBean itemBean=new ItemBean(R.drawable.btn_4,"信息上传");
        datas.add(itemBean);
        itemBean=new ItemBean(R.drawable.btn_3,"单兵图传");
        datas.add(itemBean);
        itemBean=new ItemBean(R.drawable.btn_5,"对讲");
        datas.add(itemBean);
        itemBean=new ItemBean(R.drawable.btn_2,"执法手册");
        datas.add(itemBean);
        itemBean=new ItemBean(R.drawable.btn_6,"地图");
        datas.add(itemBean);
        itemBean=new ItemBean(R.drawable.btn_1,"历史消息");
        datas.add(itemBean);
        myAdapter=new MyAdapter(this,datas);

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_sign_in= (Button) findViewById(R.id.btn_sign_in);

        if(preferences.getBoolean("isOnWork",false)){
            //btn_sign_in.setEnabled(false);
            btn_sign_in.setText("签出");
            //btn_sign_in.setBackgroundColor(Color.parseColor("#aa00ff00"));
            btn_sign_in.setBackground(getDrawable(R.drawable.plugin_camera_send_unselected));
        }else{
            //btn_sign_in.setEnabled(true);
            btn_sign_in.setText("签到");
            //btn_sign_in.setBackgroundColor(Color.parseColor("#aaff0000"));
            btn_sign_in.setBackground(getDrawable(R.drawable.plugin_camera_send_pressed));
        }

        gridView= (GridView) findViewById(R.id.gv_main_menu);
        gridView.setAdapter(myAdapter);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Log.i(TAG,"CASE 0");
                        startActivity(new Intent(MainActivity.this,MsgPhotoUpActivity.class));
                        break;
                    case 1:
                        Log.i(TAG,"CASE 1");
                        /*startActivity(new Intent(MainActivity.this,MsgPhotoUpActivity.class));*/
                        break;
                    case 2:
                        Log.i(TAG,"CASE 2");
                        /*startActivity(new Intent(MainActivity.this,MsgPhotoUpActivity.class));*/
                        break;
                    case 3:
                        Log.i(TAG,"CASE 3");
                        /*startActivity(new Intent(MainActivity.this,MsgPhotoUpActivity.class));*/
                        break;
                    case 4:
                        Log.i(TAG,"CASE 4");
                        startActivity(new Intent(MainActivity.this,MapActivity.class));
                        break;
                    case 5:
                        Log.i(TAG,"CASE 5");
                        startActivity(new Intent(MainActivity.this,NotificationHistoryActivity.class));
                        break;

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {
            Intent intent=new Intent(MainActivity.this,MsgPhotoUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent=new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent=new Intent(MainActivity.this,NotificationHistoryActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.KEYCODE_BACK==keyCode){
            Intent intent= new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }*/


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            editor.putBoolean("LoginStatus",false);
            editor.apply();
            //new UserLogout(preferences).start();
            RequestParams params = new RequestParams();
            params.put("policeNumber",preferences.getString("number", ""));
            asyncHttpService.get("appController/logout.do",params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG, "onSuccess: 退出应用"+response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(TAG, "onFailure: "+errorResponse.toString());
                }
            });

            Intent intent= new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
            System.exit(0);
        }
    }
}
