package com.eduinfinity.dimu.translatehelper.activity;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.SeekBar;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TranslateActivity extends FragmentActivity implements OnBufferingUpdateListener,
        OnCompletionListener, OnPreparedListener, TextureView.SurfaceTextureListener {
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

    //for vedio
    private boolean isVideoPlayerOK = true;
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private TextView videoInfo;
    private SeekBar seekBar;
    private String path;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private boolean isDragSrtItem = false;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_SUSPEND = 6;
    private static final int STATE_RESUME = 7;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;
    private static int mCurrentState;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        startVideo();
        startTimedText();
    }

    @Override
    protected void onPause() {
        Log.i("Activity", "Activity on pause");
        super.onPause();
        translateBus.post(new SaveEvent());
        pauseVideo();
        stopTimedText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
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
                        if (mIsVideoReadyToBePlayed) {
                            mMediaPlayer.seekTo(time);
                        }
                        isDragSrtItem = false;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                        if (i == ViewPager.SCROLL_STATE_DRAGGING) isDragSrtItem = true;
                    }
                }

        );

        videoInfo = (TextView) findViewById(R.id.videoInfo);

        mTextureView = (TextureView) findViewById(R.id.surface);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsVideoReadyToBePlayed) return;
                if (mMediaPlayer.isPlaying()) {
                    pauseVideo();
                } else {
                    startVideo();
                }
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax((int) lineList.get(lineList.size() - 1).endTime);
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
                        if (mIsVideoReadyToBePlayed && mMediaPlayer.isPlaying())
                            mMediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
        );
    }

    private void pauseVideo() {
        if (!mIsVideoReadyToBePlayed) return;
        if (mMediaPlayer.isPlaying()) {
            TranslateActivity.this.findViewById(R.id.stop).setVisibility(View.VISIBLE);
            mMediaPlayer.stop();
            mMediaPlayer.getTimedTextTrack();
            mMediaPlayer.deselectTrack(1);
            mMediaPlayer.selectTrack(1);
        }
    }

    private void startVideo() {
        if (!mIsVideoReadyToBePlayed) return;
        if (!mMediaPlayer.isPlaying()) {
            TranslateActivity.this.findViewById(R.id.stop).setVisibility(View.INVISIBLE);
            int index = mViewPager.getCurrentItem();
            long time = lineList.get(index).startTime;
            mMediaPlayer.start();
            mMediaPlayer.seekTo(time);
        }
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


    private void playVideo(SurfaceTexture surfaceTexture) {
        doCleanUp();
        path = "";
        try {
            if (FileUtils.isExist("/" + projectSlug + Config.VideosFolder, resourceSlug + ".mp4")) {
                path = FileUtils.getFileRootPath() + "/" + projectSlug + Config.VideosFolder + "/" + resourceSlug + ".mp4";
            }
            if (path == "") {
                // Tell the user to provide a media file URL.
                videoInfo.setText(R.string.no_video);
                videoInfo.setVisibility(View.VISIBLE);
                return;
            }
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this, true);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setSurface(new Surface(surfaceTexture));
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setOnTimedTextListener(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        // Log.d(TAG, "onBufferingUpdate percent:" + percent);
    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        String srtPath = FileUtils.getFileRootPath() + "/" + projectSlug + Config.SourceFolder + "/" + resourceSlug + ".srt";
        mMediaPlayer.addTimedTextSource(srtPath);
        mMediaPlayer.setTimedTextShown(true);
        if (mIsVideoReadyToBePlayed) {
            startVideoPlayback();
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        adjustAspectRatio(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
        mMediaPlayer.start();
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    /**
     * Sets the TextureView transform to preserve the aspect ratio of the video.
     */
    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight + " view=" + viewWidth + "x" + viewHeight
                + " newView=" + newWidth + "x" + newHeight + " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        mTextureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        mTextureView.setTransform(txform);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        playVideo(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private Timer textTimer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mIsVideoReadyToBePlayed && mMediaPlayer.isPlaying()) {
                int current = mViewPager.getCurrentItem();
                int dec = getSrtIndex(mMediaPlayer.getCurrentPosition() + 200);
                if (dec != current) translateBus.post(new TextChange(dec));
            }
        }
    };

    private void startTimedText() {
        textTimer.schedule(timerTask, 0, 500);
    }

    private void stopTimedText() {
        textTimer.purge();
        textTimer.cancel();
    }

    private class TextChange {
        public final int index;

        TextChange(int index) {
            this.index = index;
        }
    }

    public void onEventMainThread(TextChange event) {
        long time = lineList.get(event.index).startTime;
        mViewPager.setCurrentItem(event.index, false);
        seekBar.setProgress((int) time);
    }
//    @Override
//    public void onTimedText(String text) {
//        Log.w(seek, "srt");
//        long time = mMediaPlayer.getCurrentPosition();
//        int index = getSrtIndex(time);
//        Log.w(seek, "timedText " + index);
//        mViewPager.setCurrentItem(getSrtIndex(time), false);
//        seekBar.setProgress((int) time);
//    }
//
//    @Override
//    public void onTimedTextUpdate(byte[] pixels, int width, int height) {
//    }

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
