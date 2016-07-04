package com.megaeyes.wjlogindemo.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.megaeyes.wjlogindemo.adapter.FolderAdapter;
import com.megaeyes.wjlogindemo.util.Bimp;
import com.megaeyes.wjlogindemo.util.PublicWay;
import com.megaeyes.wjlogindemo.util.Res;
import com.megaeyes.wjlogindemo.R;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:06
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//清空选择的图片
				Bimp.tempSelectBitmap.clear();
				Intent intent = new Intent();
				intent.setClass(mContext, MsgPhotoUpActivity.class);
				startActivity(intent);
			}
		});
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText(Res.getString("photo"));
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(mContext, MsgPhotoUpActivity.class);
			startActivity(intent);
		}
		return true;
	}
}
