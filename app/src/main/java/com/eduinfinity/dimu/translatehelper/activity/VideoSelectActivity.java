package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.SrtParse;

import java.util.ArrayList;
import java.util.List;

public class VideoSelectActivity extends Activity {
    public static final String VIDEO_PATH = "VIDEO_PATH";
    private List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select);
        getVideoList();
        ListView listView = (ListView) findViewById(R.id.video_list);
        listView.setAdapter(new VideoAdapter(videoInfoList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(VIDEO_PATH, videoInfoList.get(position).file);
                setResult(RESULT_OK, intent);
                VideoSelectActivity.this.finish();
            }
        });
    }

    public void getVideoList() {
        //query external Video
        final String track_title = MediaStore.Video.Media.TITLE;
        final String track_duration = MediaStore.Video.Media.DURATION;
        final String track_data = MediaStore.Video.Media.DATA;
        final String track_size = MediaStore.Video.Media.SIZE;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        videoInfoList.clear();
        String[] columns = {track_title, track_duration, track_data, track_size};
        ContentResolver musicResolver = getContentResolver();
        Cursor cursor = musicResolver.query(uri, columns, null, null, null);
        //iterate over results if valid
        if (cursor != null && cursor.moveToFirst()) {
            //get columns
            int titleColumn = cursor.getColumnIndex(track_title);
            int durationColumn = cursor.getColumnIndex(track_duration);
            int dataColumn = cursor.getColumnIndex(track_data);
            int sizeColumn = cursor.getColumnIndex(track_size);
            //add songs to list
            do {
                String thisTitle = cursor.getString(titleColumn);
                String data = cursor.getString(dataColumn);
                Long duration = cursor.getLong(durationColumn);
                Long size = cursor.getLong(sizeColumn);
                if (!data.equals(""))
                    videoInfoList.add(new VideoInfo(thisTitle, data, duration, size));
            }
            while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
    }

    class VideoAdapter extends BaseAdapter {
        private List<VideoInfo> videoInfoList;

        public VideoAdapter(List<VideoInfo> videoInfoList) {
            this.videoInfoList = videoInfoList;
        }

        @Override
        public int getCount() {
            return videoInfoList.size();
        }

        @Override
        public VideoInfo getItem(int position) {
            return videoInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Handle handle = new Handle();
            if (convertView == null) {
                convertView = View.inflate(VideoSelectActivity.this, R.layout.video_item, null);
                handle.tv_name = (TextView) convertView.findViewById(R.id.name);
                handle.tv_size = (TextView) convertView.findViewById(R.id.size);
                handle.tv_duration = (TextView) convertView.findViewById(R.id.duration);
                convertView.setTag(handle);
            } else {
                handle = (Handle) convertView.getTag();
            }
            VideoInfo videoInfo = getItem(position);
            handle.tv_name.setText(videoInfo.name);
            handle.tv_size.setText(FileUtils.size2String(videoInfo.size));
            handle.tv_duration.setText(SrtParse.convertTime2String(videoInfo.duration));
            return convertView;
        }

        final class Handle {
            public TextView tv_name, tv_size, tv_duration;

        }
    }

    class VideoInfo {
        public String name, file;
        public Long duration, size;

        VideoInfo(String name, String file, Long duration, Long size) {
            this.name = name;
            this.file = file;
            this.duration = duration;
            this.size = size;
        }
    }

}
