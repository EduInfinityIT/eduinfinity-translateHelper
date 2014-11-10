package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.SrtViewPageAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;

import java.util.ArrayList;
import java.util.List;


public class TranslateActivity extends Activity {
    private static final String TAG = "TranslateActivity";

    public static final String ResourceSlug = "ResourceSlug";
    public static final String ProjectSlug = "ProjectSlug";
    public static final String STATUS = "Status";

    private ViewPager mViewPager;
    private LayoutInflater inflate;
    private List<View> viewList = new ArrayList<View>();
    private List<TextTrackImpl.Line> lineList;
    private SrtViewPageAdapter srtViewPageAdapter;

    private String resourceSlug, projectSlug;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);

        Intent intent = getIntent();
        resourceSlug = intent.getStringExtra(ResourceSlug);
        projectSlug = intent.getStringExtra(ProjectSlug);
        status = intent.getIntExtra(STATUS, 0);
        srtViewPageAdapter = new SrtViewPageAdapter(viewList);
        mViewPager.setAdapter(srtViewPageAdapter);

        switch (status) {
            case Model.INIT:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
                break;
//            case Model.RES_DOWNED:
//                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
//                break;
            default:
                loadSrt();
                break;
        }
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
        for (int i = 0; i < lineList.size(); i++) {
            View view = inflate.inflate(R.layout.viewpage_srt, null);
            TextView index = (TextView) view.findViewById(R.id.textView_index);
            TextView s = (TextView) view.findViewById(R.id.textView_startTime);
            TextView e = (TextView) view.findViewById(R.id.textView_endTime);
            TextView l = (TextView) view.findViewById(R.id.textView_lineString);
            EditText t = (EditText) view.findViewById(R.id.editText_trans);

            index.setText("" + lineList.get(i).index);
            s.setText("" + SrtParse.convertTime2String(lineList.get(i).startTime));
            e.setText("" + SrtParse.convertTime2String(lineList.get(i).endTime));
            l.setText("" + lineList.get(i).lineString);
            t.setText("" + lineList.get(i).getTrans());
            PageHolder holder = new PageHolder();
            holder.editText = t;
            view.setTag(holder);
            viewList.add(view);
        }
        Log.e(TAG, "" + lineList.size());
        Log.e(TAG, "" + viewList.size());
        srtViewPageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        if (lineList != null) {
            for (int i = 0; i < lineList.size(); i++) {
                PageHolder holder = (PageHolder) viewList.get(i).getTag();
                String trans = holder.editText.getText().toString().trim();
                lineList.get(i).setTrans(trans);
            }
            String allString = SrtParse.convertSrt2String(lineList);
            FileUtils.writeFileOUTStorage("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt", allString, this);

            Center.getInstance().getCurrentProject().getResource(resourceSlug).setStatus(Model.CHANGED);
        }
        super.onPause();
    }

    static class PageHolder {
        EditText editText;
    }
}
