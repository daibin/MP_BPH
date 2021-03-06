package com.megaeyes.wjlogindemo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.megaeyes.wjlogindemo.adapter.AlbumGridViewAdapter;
import com.megaeyes.wjlogindemo.util.AlbumHelper;
import com.megaeyes.wjlogindemo.util.Bimp;
import com.megaeyes.wjlogindemo.util.ImageBucket;
import com.megaeyes.wjlogindemo.util.ImageItem;
import com.megaeyes.wjlogindemo.util.PublicWay;
import com.megaeyes.wjlogindemo.util.Res;
import com.megaeyes.wjlogindemo.R;

/**
 * 这个是进入相册显示所有图片的界面
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:47:15
 */
public class AlbumActivity extends Activity {

	private GridView gridView;//显示手机里的所有图片的列表控件
	//当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;//gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	private Button okButton;//完成按钮
	private Button back;// 返回按钮
	private Button cancel;// 取消按钮
	private Intent intent;// 预览按钮
	private Button preview;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		PublicWay.activityList.add(this);
		mContext = this;
		//注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter);  
        bitmap = BitmapFactory.decodeResource(getResources(),Res.getDrawableID("plugin_camera_no_pictures"));
        init();
		initListener();
		//这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
        	//mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(int i = 0; i<contentList.size(); i++){
			dataList.addAll( contentList.get(i).imageList );
		}

        okButton= (Button) findViewById(R.id.ok_button);
        back= (Button) findViewById(R.id.back);
        cancel= (Button) findViewById(R.id.cancel);
        preview= (Button) findViewById(R.id.preview);
        tv= (TextView) findViewById(R.id.myText);
        gridView= (GridView) findViewById(R.id.myGrid);

		cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                intent.setClass(mContext, MsgPhotoUpActivity.class);
                startActivity(intent);
            }
        });
		back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(AlbumActivity.this, ImageFile.class);
                startActivity(intent);
            }
        });
		preview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Bimp.tempSelectBitmap.size() > 0) {
                    intent.putExtra("position", "1");
                    intent.setClass(AlbumActivity.this, GalleryActivity.class);
                    startActivity(intent);
                }
            }
        });
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		gridView.setEmptyView(tv);
		okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
	}

	private void initListener() {
		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,Button chooseBt) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
							toggleButton.setChecked(false);
							chooseBt.setVisibility(View.GONE);
							if (!removeOneData(dataList.get(position))) {
								Toast.makeText(AlbumActivity.this, Res.getString("only_choose_num"), Toast.LENGTH_SHORT).show();
							}
							return;
						}
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                intent.setClass(mContext, MsgPhotoUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
	}

	private boolean removeOneData(ImageItem imageItem) {
			if (Bimp.tempSelectBitmap.contains(imageItem)) {
				Bimp.tempSelectBitmap.remove(imageItem);
				okButton.setText(Res.getString("finish")+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				return true;
			}
		return false;
	}
	
	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
		return false;

	}
@Override
protected void onRestart() {
	isShowOkBt();
	super.onRestart();
}
}
