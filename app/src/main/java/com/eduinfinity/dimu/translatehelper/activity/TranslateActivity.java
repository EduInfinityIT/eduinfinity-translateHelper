package com.eduinfinity.dimu.translatehelper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.activity.adapter.SrtViewPageAdapter;

import java.util.ArrayList;


public class TranslateActivity extends Activity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);
        mViewPager.setAdapter(new SrtViewPageAdapter());
    }


}
