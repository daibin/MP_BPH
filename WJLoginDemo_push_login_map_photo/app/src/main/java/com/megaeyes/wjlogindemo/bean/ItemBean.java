package com.megaeyes.wjlogindemo.bean;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by John on 2016/4/15.
 */
public class ItemBean {
    private int imageResId;
    private String desc;

    public ItemBean(){}

    public ItemBean(int imageResId, String desc) {
        this.imageResId = imageResId;
        this.desc = desc;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
