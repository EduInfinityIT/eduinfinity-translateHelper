package com.eduinfinity.dimu.translatehelper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eduinfinity.dimu.translatehelper.View.PageFragment;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

import java.util.List;

/**
 * Created by Dimu on 11/20/14.
 */

public class SrtPageAdapter extends FragmentPagerAdapter {
    private List<TextTrackImpl.Line> lineList;


    public SrtPageAdapter(FragmentManager fm, List<TextTrackImpl.Line> lineList) {
        super(fm);
        this.lineList = lineList;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(lineList.get(position));
    }

    @Override
    public int getCount() {
        return lineList.size();
    }

}


