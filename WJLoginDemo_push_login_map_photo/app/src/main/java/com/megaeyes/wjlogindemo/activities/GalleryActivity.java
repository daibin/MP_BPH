package com.megaeyes.wjlogindemo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.megaeyes.wjlogindemo.adapter.MyPageAdapter;
import com.megaeyes.wjlogindemo.util.Bimp;
import com.megaeyes.wjlogindemo.util.PublicWay;
import com.megaeyes.wjlogindemo.util.Res;
import com.megaeyes.wjlogindemo.zoom.PhotoView;
import com.megaeyes.wjlogindemo.zoom.ViewPagerFixed;
import com.megaeyes.wjlogindemo.R;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:47:53
 */
public class GalleryActivity extends Activity {
	private Intent intent;
    // 返回按钮
    private Button back_bt;
	// 发送按钮
	private Button send_bt;
	//删除按钮
	private Button del_bt;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	private int position;
	//当前的位置
	private int location = 0;
	
	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	
	private Context mContext;

	RelativeLayout photo_relativeLayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_gallery"));// 切屏到主界面
		PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(R.id.gallery_back);
		send_bt = (Button) findViewById(R.id.send_button);
		del_bt = (Button)findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(GalleryActivity.this, ImageFile.class);
				startActivity(intent);
			}
		});
		send_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				intent.setClass(mContext,MsgPhotoUpActivity.class);
				startActivity(intent);
			}
		});
		del_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listViews.size() == 1) {
					Bimp.tempSelectBitmap.clear();
					Bimp.max = 0;
					send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
					Intent intent = new Intent("data.broadcast.action");
					sendBroadcast(intent);
					finish();
				} else {
					Bimp.tempSelectBitmap.remove(location);
					Bimp.max--;
					pager.removeAllViews();
					listViews.remove(location);
					adapter.setListViews(listViews);
					send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
					adapter.notifyDataSetChanged();
				}
			}
		});
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		position = Integer.parseInt(intent.getStringExtra("position"));
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews( Bimp.tempSelectBitmap.get(i).getBitmap() );
		}
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};
	
	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}
	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(position==1){
				this.finish();
				intent.setClass(GalleryActivity.this, AlbumActivity.class);
				startActivity(intent);
			}else if(position==2){
				this.finish();
				intent.setClass(GalleryActivity.this, ShowAllPhoto.class);
				startActivity(intent);
			}
		}
		return true;
	}
}
