/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.messagepush.demoapp;

import java.util.ArrayList;
import java.util.List;


import com.example.messagepush.client.NotificationHistoryActivity;
import com.example.messagepush.client.ServiceManager;
import com.megaeyes.wjlogindemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This is an androidpn client demo application.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class DemoAppActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DemoAppActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Settings
        Button okButton = (Button) findViewById(R.id.btn_settings);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ServiceManager.viewNotificationSettings(DemoAppActivity.this);
            }
        });
        
        Button historyButton=(Button) findViewById(R.id.btn_histories);
        historyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(DemoAppActivity.this,NotificationHistoryActivity.class);
				startActivity(intent);
			}
		});

        // Start the service
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
        serviceManager.setAlias("daibin");
        List<String> tagsList=new ArrayList<String>();
        tagsList.add("sports");
        tagsList.add("music");
        serviceManager.setTags(tagsList);
    }

}