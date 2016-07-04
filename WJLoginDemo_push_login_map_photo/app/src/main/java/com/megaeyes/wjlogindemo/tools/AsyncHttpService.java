package com.megaeyes.wjlogindemo.tools;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;


/**
 * Created by John on 2016-05-19.
 */
public class AsyncHttpService {
    private static final String TAG = "AsyncHttpService";
    private  Context context;
    private AsyncHttpClient client;
    private PersistentCookieStore cookieStore;

    /*public AsyncHttpService() {
        client=new AsyncHttpClient();
    }

    public AsyncHttpService(Context context) {
        this.context = context;
        client=new AsyncHttpClient();
    }*/

    private volatile static AsyncHttpService asyncHttpService;
    private AsyncHttpService (Context context){
        this.context = context;
        client=new AsyncHttpClient();
    }
    public static AsyncHttpService getAsyncHttpServiceInstance(Context context) {
        if (asyncHttpService == null) {
            synchronized (AsyncHttpService.class) {
                if (asyncHttpService == null) {
                    asyncHttpService = new AsyncHttpService(context);
                }
            }
        }
        return asyncHttpService;
    }

    private  final String BASE_URL = "http://219.138.66.104:8080/BPHCenter/";

    public  void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public  void get(String url,AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public  void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private  String getAbsoluteUrl(String relativeUrl) {
        Log.i(TAG,BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

    public  void saveCookie(){
        cookieStore=new PersistentCookieStore(context);
        client.setCookieStore(cookieStore);
    }
    public List<Cookie> getCookies(){
        List<Cookie> cookies=new ArrayList<Cookie>();
        cookies=cookieStore.getCookies();
        for(Cookie cookie:cookies){
            Log.i(TAG,cookie+"");
        }
        return cookies;
    }
}
