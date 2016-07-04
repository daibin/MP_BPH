package com.megaeyes.wjlogindemo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megaeyes.wjlogindemo.R;
import com.megaeyes.wjlogindemo.service.MapService;
import com.megaeyes.wjlogindemo.tools.AsyncHttpService;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends AppCompatActivity {

    protected static final String TAG = "LoginActivity";
    protected static final int ENTER_HOME = 0;
    protected static final int URL_ERROR = 1;
    protected static final int IO_ERROR = 2;
    protected static final int JSON_ERROR = 3;
    protected static final int LOGIN_ERROR = 300;
    protected static final int SERVER_ERROR = 500;
    private static final int CONN_TME_OUT = 4;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private Button mUsernameSignInButton;
    private String description;
    private SharedPreferences preferences;
    private CheckBox checkBox;
    private Editor editor;

    private AsyncHttpService asyncHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        asyncHttpService=AsyncHttpService.getAsyncHttpServiceInstance(LoginActivity.this);
        preferences=getSharedPreferences("CookiePrefsFile", Activity.MODE_PRIVATE);
        editor=preferences.edit();
        Log.i("TAG",preferences.getBoolean("LoginStatus",false)+"1");
        if(preferences.getBoolean("LoginStatus",false)){
            enterHome();
        }

        initView();
        if(preferences.getBoolean("isChecked",false)){
            checkBox.setChecked(true);
            mUsernameView.setText(preferences.getString("username",""));
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   editor.putBoolean("isChecked",true);
                }else{
                    editor.putBoolean("isChecked",false);;
                }
            }
        });
        
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume()");
    }

    @Override
    protected void onDestroy() {
        editor.putBoolean("LoginStatus",false);
        editor.commit();
        Log.i("TAG",preferences.getBoolean("LoginStatus",false)+"3");
        super.onDestroy();
    }

    private void initView() {
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mUsernameSignInButton = (Button) findViewById(R.id.sign_in_button);
        checkBox= (CheckBox) findViewById(R.id.cb_remember_username);
    }


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ENTER_HOME:
                    //Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Log.i("TAG",preferences.getBoolean("LoginStatus",false)+"5");
                    enterHome();
                    break;
                case LOGIN_ERROR:
                   Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
                   /* if("密码错误！".equals(description)){
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                    }
                    if("用户不存在！".equals(description)){
                        mUsernameView.setError(getString(R.string.error_invalid_username));
                    }*/
                    break;
                case SERVER_ERROR:
                    Toast.makeText(LoginActivity.this,"连不上服务器……",Toast.LENGTH_LONG).show();
                    break;
                case URL_ERROR:
                    Toast.makeText(LoginActivity.this,"URL错误，请与管理员联系！",Toast.LENGTH_LONG).show();
                    break;
                case IO_ERROR:
                    Toast.makeText(LoginActivity.this,"网络错误，请稍后再试……",Toast.LENGTH_LONG).show();
                    break;
                case JSON_ERROR:
                    Toast.makeText(LoginActivity.this,"JSON解析错误，请与管理员联系！",Toast.LENGTH_LONG).show();
                    break;
                case CONN_TME_OUT:
                    Toast.makeText(LoginActivity.this,"连接服务器超时，请检查网络……",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    private void enterHome() {
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        Intent intentGpsInfo=new Intent(LoginActivity.this, MapService.class);
        startService(intentGpsInfo);
    }

    private void attemptLogin() {

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            RequestParams params = new RequestParams();
            params.put("policeNumber", username);
            params.put("password", password);
            asyncHttpService.saveCookie();

            asyncHttpService.get("appController/login.do", params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.i(TAG,response.toString());
                    asyncHttpService.getCookies();
                    Message message=Message.obtain();
                    try {
                        int code = response.getInt("code");
                        Log.i(TAG,"---------------------------------------------------------------"+code+"---------------------------------------------------------------");
                        description = (String) response.get("description");
                        Log.i(TAG,description);
                        if(200==code){
                            JSONObject obj2=(JSONObject) response.get("data");
                            int orgId = obj2.getInt("orgId");
                            int id = obj2.getInt("id");
                            String name = obj2.getString("name");
                            String number = obj2.getString("number");
                            String msg = obj2.getString("msg");
                            editor.putInt("orgId", orgId);
                            editor.putInt("id", id);
                            editor.putString("name", name);
                            editor.putString("number", number);
                            editor.putBoolean("LoginStatus",true);
                            message.what = ENTER_HOME;
                        }else{
                            editor.putBoolean("LoginStatus",false);
                            message.what = LOGIN_ERROR;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what=JSON_ERROR;
                        editor.putBoolean("LoginStatus",false);
                    }finally{
                        editor.apply();
                        System.out.println("editor.commit();");
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Message message=Message.obtain();
                    message.what =CONN_TME_OUT;
                    handler.sendMessage(message);
                }
            });
        }
    }
}

