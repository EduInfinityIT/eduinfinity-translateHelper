package com.eduinfinity.dimu.translatehelper.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;

import java.util.List;

/**
 * Created by Dimu on 10/22/14.
 */
public class ModelListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<Model> mList;
    private Context mContext;

    public ModelListAdapter(Context context, List<Model> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return getItem(position).getStatus();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Model getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.file_item, null);
            viewHolder.fileName = (TextView) convertView.findViewById(R.id.textView_file_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.fileName.setText(getItem(position).getValue(Resource.NAME));
        int[] colors = {0xddcb5050, 0xdde1776f, 0xdde5e293, 0xdd78abf2, 0xdd5cc45f};
        convertView.setBackgroundColor(colors[getItem(position).getStatus()]);
        return convertView;
    }

    public void up2first(int position) {
        Model model = mList.get(position);
        mList.remove(position);
        mList.add(0, model);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView fileName;
    }
}
