package com.eduinfinity.dimu.translatehelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.SrtPageAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class TranslateActivity extends FragmentActivity {
    private static final String TAG = "TranslateActivity";

    public static final String ResourceSlug = "ResourceSlug";
    public static final String ProjectSlug = "ProjectSlug";
    public static final String STATUS = "Status";
    public static final String ResourceName = "ProjectName";

    private ViewPager mViewPager;
    private LayoutInflater inflate;
    private List<View> viewList = new ArrayList<View>();
    private List<TextTrackImpl.Line> lineList;
    private PagerAdapter srtAdapter;

    private String resourceSlug, projectSlug, name;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translateBus.register(this);
        setContentView(R.layout.activity_translate);
        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);
        Intent intent = getIntent();
        resourceSlug = intent.getStringExtra(ResourceSlug);
        projectSlug = intent.getStringExtra(ProjectSlug);
        name = intent.getStringExtra(ResourceName);
        status = intent.getIntExtra(STATUS, 0);
        setTitle(name);
        loadSrt();
        srtAdapter = new SrtPageAdapter(getSupportFragmentManager(), lineList);
        mViewPager.setAdapter(srtAdapter);
//        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circlePageIndicator.setViewPager(mViewPager);
//        srtAdapter = new SrtViewPageAdapter(viewList);
//        mViewPager.setAdapter(srtAdapter);


//        switch (status) {
//            case Model.INIT:
//                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
//                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
//                break;
////            case Model.RES_DOWNED:
////                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
////                break;
//            default:
//
//                break;
//        }
//        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circlePageIndicator.setViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadSrt();
    }

    private void loadSrt() {
        inflate = getLayoutInflater();
        TextTrackImpl tack = new TextTrackImpl();
        if (FileUtils.isExist("/" + projectSlug + Config.SourceFolder, resourceSlug + ".srt")) {
            FileUtils.readRes2track("/" + projectSlug + Config.SourceFolder, resourceSlug + ".srt", tack, this);
        }
        if (FileUtils.isExist("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt")) {
            FileUtils.readTrans2track("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt", tack, this);
        }
        if (FileUtils.isExist("/" + projectSlug + Config.VideosFolder, resourceSlug + ".mp4")) {
        }

        lineList = tack.getSubs();
//        for (int i = 0; i < lineList.size(); i++) {
//            View view = inflate.inflate(R.layout.viewpage_srt, null);
//            TextView index = (TextView) view.findViewById(R.id.textView_index);
//            TextView s = (TextView) view.findViewById(R.id.textView_startTime);
//            TextView e = (TextView) view.findViewById(R.id.textView_endTime);
//            TextView l = (TextView) view.findViewById(R.id.textView_lineString);
//            EditText t = (EditText) view.findViewById(R.id.editText_trans);
//
//            index.setText("" + lineList.get(i).index);
//            s.setText("" + SrtParse.convertTime2String(lineList.get(i).startTime));
//            e.setText("" + SrtParse.convertTime2String(lineList.get(i).endTime));
//            l.setText("" + lineList.get(i).lineString);
//            t.setText("" + lineList.get(i).getTrans());
//            PageHolder holder = new PageHolder();
//            holder.editText = t;
//            view.setTag(holder);
//            viewList.add(view);
//        }
//        Log.e(TAG, "" + lineList.size());
//        Log.e(TAG, "" + viewList.size());
//        if (viewList.size() == 0)
//            Toast.makeText(this, R.string.parseSRTError, Toast.LENGTH_SHORT).show();
//        srtAdapter.notifyDataSetChanged();
    }


    EventBus translateBus = new EventBus();

    @Override
    protected void onPause() {
        Log.i("Activity", "Activity on pause");
        translateBus.post(new SaveEvent());
        super.onPause();
    }

    public void onEventAsync(SaveEvent event) {
        if (lineList != null) {
            String allString = SrtParse.convertSrt2String(lineList);
            FileUtils.writeFileOUTStorage("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt", allString, this);
            Center.getInstance().getCurrentProject().getResource(resourceSlug).setStatus(Model.CHANGED);
        }
    }

    class SaveEvent {

    }

}
