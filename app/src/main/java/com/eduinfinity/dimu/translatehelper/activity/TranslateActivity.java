package com.eduinfinity.dimu.translatehelper.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.SrtPageAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;
import com.eduinfinity.dimu.translatehelper.utils.TextTrackImpl;
import de.greenrobot.event.EventBus;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class TranslateActivity extends FragmentActivity implements MediaPlayer.OnTimedTextListener {
    private static final String TAG = "TranslateActivity";

    public static final String ResourceSlug = "ResourceSlug";
    public static final String ProjectSlug = "ProjectSlug";
    public static final String VIDEO_PATH = "VIDEO_PATH";
    public static final String STATUS = "Status";
    public static final String ResourceName = "ProjectName";
    private static final int SELECT_VIDEO = 1;

    private ViewPager mViewPager;
    private List<TextTrackImpl.Line> lineList;
    private PagerAdapter srtAdapter;

    private String resourceSlug, projectSlug, name;
    private EventBus translateBus = new EventBus();

    private boolean isVideoPlayerOK = true;
    private boolean isDragSrtItem;
    private VideoView mVideoView;
    private SeekBar seekBar;
    private View stop, video_layout;
    private String videoPath;
    private int previousProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        translateBus.register(this);

        Intent intent = getIntent();
        resourceSlug = intent.getStringExtra(ResourceSlug);
        projectSlug = intent.getStringExtra(ProjectSlug);
        videoPath = intent.getStringExtra(VIDEO_PATH);
        name = intent.getStringExtra(ResourceName);

        setTitle(name);

        loadSrt();
        if (!LibsChecker.checkVitamioLibs(this)) {
            Toast.makeText(this, R.string.errorVideoLib, Toast.LENGTH_SHORT).show();
            isVideoPlayerOK = false;
        }
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
            if (!isVideoPlayerOK) {
                Toast.makeText(this, R.string.errorVideoLib, Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(TranslateActivity.this, VideoSelectActivity.class);
            startActivityForResult(intent, SELECT_VIDEO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == SELECT_VIDEO) {
            String path = data.getStringExtra(VideoSelectActivity.VIDEO_PATH);
            if (!path.equals("")) {
                mVideoView.setVideoPath(path);
                Center.getInstance().getProject(projectSlug).getResource(resourceSlug).putValue(Resource.VIDEO, path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
        stop.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        Log.i("Activity", "Activity on pause");
        super.onPause();
        mVideoView.pause();
        translateBus.post(new SaveEvent());
    }

    @Override
    protected void onDestroy() {
        mVideoView.stopPlayback();
        super.onDestroy();
    }

    private void initViewAndSetListener() {
        stop = findViewById(R.id.stop);
        video_layout = findViewById(R.id.video_layout);
        video_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    stop.setVisibility(View.VISIBLE);
                    mVideoView.pause();
                } else {
                    stop.setVisibility(View.GONE);
                    mVideoView.start();
                    mVideoView.seekTo(seekBar.getProgress());
                }

            }
        });
        mViewPager = (ViewPager) findViewById(R.id.subtitle_list_pager);
        srtAdapter = new SrtPageAdapter(getSupportFragmentManager(), lineList);
        mViewPager.setAdapter(srtAdapter);
//        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        circlePageIndicator.setViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int i) {
                        if (!isDragSrtItem) return;

                        long time = lineList.get(i).startTime;
                        seekBar.setProgress((int) time);
                        if (mVideoView.isPlaying()) mVideoView.seekTo(time);
                        isDragSrtItem = false;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                        if (i == ViewPager.SCROLL_STATE_DRAGGING) isDragSrtItem = true;
                    }
                }

        );
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setMax((int) lineList.get(lineList.size() - 1).endTime);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && progress > previousProgress) {
                            seekBar.setThumb(getResources().getDrawable(R.drawable.red_arrow));
                        }
                        if (fromUser && progress < previousProgress) {
                            seekBar.setThumb(getResources().getDrawable(R.drawable.red_arrow_back));
                        }
                        previousProgress = progress;

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        previousProgress = seekBar.getProgress();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mViewPager.setCurrentItem(getSrtIndex(seekBar.getProgress()), false);
                        if (mVideoView.isPlaying())
                            mVideoView.seekTo(seekBar.getProgress());
                        seekBar.setThumb(getResources().getDrawable(R.drawable.red_line_l));
                    }
                }
        );


    }

    private int getSrtIndex(long location) {
        Log.i(TAG, "location " + location);
        //TODO how to get current page quickly
//        int indexMaybe = (int) (location / 3000);
//        if (indexMaybe > lineList.size() - 1) indexMaybe = lineList.size() - 1;
//        TextTrackImpl.Line line = lineList.get(indexMaybe);
//        do {
//            if (location < line.startTime) indexMaybe--;
//            if (location > line.endTime) indexMaybe++;
//            if (indexMaybe < 0) return 0;
//            if (indexMaybe > lineList.size() - 1) return lineList.size() - 1;
//            line = lineList.get(indexMaybe);
//        } while (location < line.startTime || location > line.endTime);
//        return indexMaybe;
        for (int i = 0; i < lineList.size(); i++) {
            if (location < lineList.get(i).endTime) return i;
        }
        return 0;
    }

    private void loadSrt() {
        TextTrackImpl tack = new TextTrackImpl();
        if (FileUtils.isExist("/" + projectSlug + Config.SourceFolder, resourceSlug + ".srt")) {
            FileUtils.readRes2track("/" + projectSlug + Config.SourceFolder, resourceSlug + ".srt", tack);
        }
        if (FileUtils.isExist("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt")) {
            FileUtils.readTrans2track("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt", tack);
        }
        lineList = tack.getSubs();
    }

    public void initPlay() {
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        if (!isVideoPlayerOK) return;
//        videoPath = FileUtils.getFileRootPath() + "/" + projectSlug + Config.VideosFolder + "/" + resourceSlug + ".mp4";
        if (!(videoPath == null || videoPath.equals(""))) {
            mVideoView.setVideoURI(Uri.parse(videoPath));
        }

//        Log.w(TAG, videoPath);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                Log.i(TAG, "onPrepared");
                mediaPlayer.setPlaybackSpeed(1.0f);
                mVideoView.addTimedTextSource(FileUtils.getFileRootPath() + "/" + projectSlug + Config.SourceFolder + "/" + resourceSlug + ".srt");
                mVideoView.setOnTimedTextListener(TranslateActivity.this);
                mVideoView.setTimedTextShown(true);
            }
        });
    }

    @Override
    public void onTimedText(String text) {
        Log.i(TAG, "");
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
            FileUtils.writeFileOUTStorage("/" + projectSlug + Config.TransFolder, resourceSlug + ".srt", allString);
            Center.getInstance().getProject(projectSlug).getResource(resourceSlug).setStatus(Model.CHANGED);
        }
    }

    class SaveEvent {
    }
}
