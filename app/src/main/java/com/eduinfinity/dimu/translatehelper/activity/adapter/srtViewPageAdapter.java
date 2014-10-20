package com.eduinfinity.dimu.translatehelper.activity.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

import java.util.List;

/**
 * Created by Dimu on 10/20/14.
 */
public class SrtViewPageAdapter extends PagerAdapter {

    private    List<TextTrackImpl.Line> subList;

    public SrtViewPageAdapter(List<TextTrackImpl.Line> subList) {
        this.subList = subList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)   {
        container.removeViewAt(position);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListViews.get(position), 0);//添加页卡
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return  subList.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==arg1;//官方提示这样写
    }


}
