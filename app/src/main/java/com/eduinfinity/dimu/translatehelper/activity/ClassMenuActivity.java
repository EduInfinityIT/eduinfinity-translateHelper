package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.FileListAdapter;
import com.eduinfinity.dimu.translatehelper.utils.FileScan;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassMenuActivity extends Activity {


    private static final String TAG = "ClassMenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_menu);

        init();
    }

    private void init() {
        if (!FileUtils.isExternalStorageWritable()) {
            Toast.makeText(this, R.string.storageUnvalide, Toast.LENGTH_SHORT).show();
            this.finish();
        }

        File file = FileUtils.initFolder(FileUtils.EduInfinityName);
        HashMap<String, String> files = FileScan.getMusicListOnSys(file);
        List<String> fileList = new ArrayList<String>();

        for (String s : files.keySet()) {
            fileList.add(s);
            Log.i(TAG, "file " + s);
        }
        ListView listView = (ListView) findViewById(R.id.listView_class_menu);
        listView.setAdapter(new FileListAdapter(getApplicationContext(), fileList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileListAdapter adapter = (FileListAdapter) parent.getAdapter();
                Intent intent = new Intent(ClassMenuActivity.this, TranslateActivity.class);
                intent.putExtra(TranslateActivity.FILENAME, adapter.getItem(position));
                startActivity(intent);
            }
        });
    }


}
