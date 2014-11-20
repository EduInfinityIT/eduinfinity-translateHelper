package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.SrtViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class HowToActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
        List<View> viewList = new ArrayList<View>();
        int[] resIds = {R.layout.guide1, R.layout.guide2, R.layout.guide3};
        for (int i = 0; i < 2; i++) {
            View view = getLayoutInflater().inflate(resIds[i], null);
            viewList.add(view);
        }
        View view = getLayoutInflater().inflate(R.layout.guide3, null);
        view.findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowToActivity.this.finish();
            }
        });
        viewList.add(view);

        ViewPager viewPager = (ViewPager) findViewById(R.id.page_how_to);
        viewPager.setAdapter(new SrtViewPageAdapter(viewList));
//        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circlePageIndicator.setViewPager(viewPager);
//        final float density = getResources().getDisplayMetrics().density;
//        int color = getResources().getColor(R.color.light_bg_border);
//        int color_bg_deep = getResources().getColor(R.color.background_deep);
//        int color_bg = getResources().getColor(R.color.background);
////        CirclePageIndicator.setBackgroundColor(0xFFCCCCCC);
//        CirclePageIndicator.setRadius(3 * density);
//        CirclePageIndicator.setPageColor(color_bg_deep);
//        CirclePageIndicator.setFillColor(color);
//        CirclePageIndicator.setStrokeColor(color_bg);
//        CirclePageIndicator.setStrokeWidth(0.5 * density);

//
//                 < !--app:fillColor="@color/light_bg_border"-->
//        <!--app:pageColor="@color/background_deep"-->
//        <!--app:radius="3dp"-->
//        <!--app:strokeColor="@color/background"-->
//        <!--app:strokeWidth="0.5dp"-->
//    }

//    class myPageAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return 3;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object o) {
//            return false;
//        }
//    }
    }

}
