package com.eduinfinity.dimu.translatehelper.activity;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.SrtPageAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;
import de.greenrobot.event.EventBus;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TranslateActivity extends FragmentActivity implements MediaPlayer.OnTimedTextListener {
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
    private EventBus translateBus = new EventBus();

    private boolean isVideoPlayerOK = true;
    private boolean isDragSrtItem;
    private VideoView mVideoView;
    private SeekBar seekBar;
    private TextView videoInfo;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        translateBus.register(this);

        Intent intent = getIntent();
        resourceSlug = intent.getStringExtra(ResourceSlug);
        projectSlug = intent.getStringExtra(ProjectSlug);
        name = intent.getStringExtra(ResourceName);
        status = intent.getIntExtra(STATUS, 0);

        setTitle(name);

        loadSrt();
        if (!LibsChecker.checkVitamioLibs(this)) isVideoPlayerOK = false;
        initViewAndSetListener();
        initPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.select_file) {
            Toast.makeText(this, "功能带做", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Activity", "Activity on pause");
        super.onPause();
        translateBus.post(new SaveEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViewAndSetListener() {
        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);
        srtAdapter = new SrtPageAdapter(getSupportFragmentManager(), lineList);
        mViewPager.setAdapter(srtAdapter);
//        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circlePageIndicator.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int i) {
                        long time = lineList.get(i).startTime;
                        if (!isDragSrtItem) return;
                        seekBar.setProgress((int) time);
                        mVideoView.seekTo(time);
                        isDragSrtItem = false;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                        if (i == ViewPager.SCROLL_STATE_DRAGGING) isDragSrtItem = true;
                    }
                }

        );
        seekBar= (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax((int) lineList.get(lineList.size()-1).endTime);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mViewPager.setCurrentItem(getSrtIndex(seekBar.getProgress()), false);
                        if (mVideoView.isPlaying())
                            mVideoView.seekTo(seekBar.getProgress());
                    }
                }
        );
    }

    private int getSrtIndex(long location) {
        int indexMaybe = (int) (location / 3000);
        if (indexMaybe > lineList.size() - 1) indexMaybe = lineList.size() - 1;
        TextTrackImpl.Line line = lineList.get(indexMaybe);
        do {
            if (location < line.startTime) indexMaybe--;
            if (location > line.endTime) indexMaybe++;
            if (indexMaybe < 0) return 0;
            if (indexMaybe > lineList.size() - 1) return lineList.size() - 1;
            line = lineList.get(indexMaybe);
        } while (location < line.startTime || location > line.endTime);
        return indexMaybe;
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
        lineList = tack.getSubs();
    }

    public void initPlay() {
        videoInfo = (TextView) findViewById(R.id.videoInfo);

        mVideoView = (VideoView) findViewById(R.id.surface_view);
        path = FileUtils.getFileRootPath() + "/" + projectSlug + Config.VideosFolder + "/" + resourceSlug + ".mp4";


        mVideoView.setVideoURI(Uri.parse(path));
        Log.w(TAG, path);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                mVideoView.addTimedTextSource( FileUtils.getFileRootPath() + "/" + projectSlug + Config.SourceFolder + "/" + resourceSlug + ".srt");
                mVideoView.setOnTimedTextListener(TranslateActivity.this);
                mVideoView.setTimedTextShown(true);
            }
        });

    }

    @Override
    public void onTimedText(String text) {
        Log.i(TAG,"");
        long time = mVideoView.getCurrentPosition();
        int index = getSrtIndex(time);
        mViewPager.setCurrentItem(index, false);
        seekBar.setProgress((int) time);
    }
    @Override
    public void onTimedTextUpdate(byte[] pixels, int width, int height) {
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
