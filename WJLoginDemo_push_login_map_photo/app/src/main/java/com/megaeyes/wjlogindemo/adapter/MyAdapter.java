package com.megaeyes.wjlogindemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.megaeyes.wjlogindemo.R;
import com.megaeyes.wjlogindemo.bean.ItemBean;

import java.util.List;

/**
 * Created by John on 2016/4/18.
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ItemBean> datas;

    public MyAdapter(Context context , List<ItemBean> datas){
        inflater=LayoutInflater.from(context);
        this.datas=datas;
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.main_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.image= (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHolder.desc= (TextView) convertView.findViewById(R.id.tv_item_desc);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ItemBean itemBean=datas.get(position);
        viewHolder.image.setImageResource(itemBean.getImageResId());
        viewHolder.desc.setText(itemBean.getDesc());
        return convertView;
    }

    private class ViewHolder{
        ImageView image;
        TextView desc;
    }
}
