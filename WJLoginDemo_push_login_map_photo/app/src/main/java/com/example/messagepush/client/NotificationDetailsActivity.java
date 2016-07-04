package com.example.messagepush.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.megaeyes.wjlogindemo.R;

public class NotificationDetailsActivity extends AppCompatActivity {

    private static final String LOGTAG = LogUtil.makeLogTag(NotificationDetailsActivity.class);
        private String callbackActivityPackageName;
        private String callbackActivityClassName;

        private String notificationId;
        private String notificationApiKey ;
        private String notificationTitle ;
        private String notificationMessage ;
        private String notificationUri ;
        private String notificationImageUrl ;

        private NetworkImageView networkImageView;
        private RequestQueue mQueue;
        private TextView tv_message;
        private Toolbar toolbar;
        private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        SharedPreferences sharedPrefs = this.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        callbackActivityPackageName = sharedPrefs.getString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
        callbackActivityClassName = sharedPrefs.getString(Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
        mQueue= Volley.newRequestQueue(NotificationDetailsActivity.this);


        getIntentParam();
        LogPrint();
        initView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (notificationUri != null
                        && notificationUri.length() > 0
                        && (notificationUri.startsWith("http:") || notificationUri.startsWith("https:")
                        || notificationUri.startsWith("tel:") || notificationUri
                        .startsWith("geo:"))) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notificationUri));
                } else {
                    intent = new Intent().setClassName(callbackActivityPackageName, callbackActivityClassName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                }

                NotificationDetailsActivity.this.startActivity(intent);
                NotificationDetailsActivity.this.finish();
            }
        });
        ImageLoader imageLoader=new ImageLoader(mQueue, new ImageLoader.ImageCache() {

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                // TODO Auto-generated method stub
                Log.i(LOGTAG,url+"****1****");
            }

            @Override
            public Bitmap getBitmap(String url) {
                // TODO Auto-generated method stub
                Log.i(LOGTAG,url.substring(url.indexOf("http://"))+"****2****");
                return null;
            }
        });
        Log.i(LOGTAG,"notificationImageUrl:"+notificationImageUrl);
        networkImageView.setImageUrl(notificationImageUrl, imageLoader);

    }

    private void getIntentParam() {

        Intent intent = getIntent();
        notificationId = intent.getStringExtra(Constants.NOTIFICATION_ID);
        notificationApiKey = intent.getStringExtra(Constants.NOTIFICATION_API_KEY);
        notificationTitle = intent.getStringExtra(Constants.NOTIFICATION_TITLE);
        notificationMessage = intent.getStringExtra(Constants.NOTIFICATION_MESSAGE);
        notificationUri = intent.getStringExtra(Constants.NOTIFICATION_URI);
        notificationImageUrl = intent.getStringExtra(Constants.NOTIFICATION_IMAGE_URL);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        networkImageView= (NetworkImageView) findViewById(R.id.niv_image);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setTitle(notificationTitle);
        tv_message= (TextView) findViewById(R.id.tv_message);
        tv_message.setText(notificationMessage);
    }

    private void LogPrint(){
        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
        Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
        Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
        Log.d(LOGTAG, "notificationUri=" + notificationUri);
        Log.d(LOGTAG, "notificationImageUrl=" + notificationImageUrl);
    }
}
