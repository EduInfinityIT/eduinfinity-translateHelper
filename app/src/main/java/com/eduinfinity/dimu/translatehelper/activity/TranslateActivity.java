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
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

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
    private String path;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

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
        if (!LibsChecker.checkVitamioLibs(this)) isVideoPlayerOK = false;
        mTextureView = (TextureView) findViewById(R.id.surface);
        mTextureView.setSurfaceTextureListener(this);
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

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(mIsVideoReadyToBePlayed){
                    mMediaPlayer.seekTo(lineList.get(i).startTime);
                    mMediaPlayer.getDuration();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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


        lineList = tack.getSubs();
    }


    @Override
    protected void onPause() {
        Log.i("Activity", "Activity on pause");
        super.onPause();
        translateBus.post(new SaveEvent());
        releaseMediaPlayer();
        doCleanUp();
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

    private void playVideo(SurfaceTexture surfaceTexture) {
        doCleanUp();
        path = "";
//        path=
        try {
            if (FileUtils.isExist("/" + projectSlug + Config.VideosFolder, resourceSlug + ".mp4")) {
                path = FileUtils.getFileRootPath() +"/"+ projectSlug + Config.VideosFolder +"/"+ resourceSlug + ".mp4";
            }
            if (path == "") {
                // Tell the user to provide a media file URL.
                Toast.makeText(TranslateActivity.this, "not find video", Toast.LENGTH_LONG).show();
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

}
