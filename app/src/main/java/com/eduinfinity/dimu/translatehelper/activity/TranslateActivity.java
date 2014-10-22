package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.activity.adapter.SrtViewPageAdapter;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

import java.util.ArrayList;
import java.util.List;


public class TranslateActivity extends Activity {
    private static final String TAG = "TranslateActivity";

    public static final String FILENAME = "FILENAME";

    private ViewPager mViewPager;
    private LayoutInflater inflate;
    private List<View> viewList = new ArrayList<View>();
    private List<TextTrackImpl.Line> lineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);


        Intent intent = getIntent();
        String fileName = intent.getStringExtra(FILENAME);

        TextTrackImpl tack = null;
        if (!fileName.equals("") || fileName != null) {
            tack = FileUtils.readFile(fileName, this);
        }
        lineList = tack.getSubs();
        Log.i(TAG, "parse " + tack.getSubs().get(0));


    }

    @Override
    protected void onResume() {
        super.onResume();
        inflate = getLayoutInflater();
        for (int i = 0; i < lineList.size(); i++) {
            View view = inflate.inflate(R.layout.viewpage_srt, null);
            TextView s = (TextView) view.findViewById(R.id.textView_startTime);
            TextView e = (TextView) view.findViewById(R.id.textView_endTime);
            TextView l = (TextView) view.findViewById(R.id.textView_lineString);
            s.setText("" + lineList.get(i).startTime);
            e.setText("" + lineList.get(i).endTime);
            l.setText("" + lineList.get(i).lineString);
            viewList.add(view);
        }
        mViewPager.setAdapter(new SrtViewPageAdapter(viewList));

    }
}
