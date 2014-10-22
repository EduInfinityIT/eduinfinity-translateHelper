package com.eduinfinity.dimu.translatehelper.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;

import java.util.List;

/**
 * Created by Dimu on 10/22/14.
 */
public class FileListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<String> mList;
    private Context mContext;

    public FileListAdapter(Context context, List<String> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
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
        viewHolder.fileName.setText(getItem(position));
        return convertView;
    }

    static class ViewHolder {
        public TextView fileName;
    }
}
