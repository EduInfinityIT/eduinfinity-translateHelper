package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.ModelListAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileScan;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ClassMenuActivity extends Activity {


    private static final String TAG = "ClassMenuActivity";
    private Center center = Center.getInstance();
    private List<Model> projectList;
    private List<Model> resourceList;
    private TextView textView_addCourse;
    private EditText editText_courseName;
    private EventBus eventBus = EventBus.getDefault();
    private TXRestClientUsage txRestClientUsage = new TXRestClientUsage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_menu);
        StorageStatus(this);
        center.setClassActivity(this);
        init();
    }

    private void init() {
        projectList = center.getProjectList();

        editText_courseName = (EditText) findViewById(R.id.editText_courseName);
        textView_addCourse = (TextView) findViewById(R.id.textView_addCourse);
        textView_addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText_courseName.getText().toString().trim();
                getProjectDetails(s);
            }
        });

        ModelListAdapter adapter = center.getResourceAdapter(this);
        ListView listView = (ListView) findViewById(R.id.listView_class_menu);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelListAdapter adapter = (ModelListAdapter) parent.getAdapter();
                Model model = adapter.getItem(position);
                int status = model.getStatus();
                if (status > 3) {
                    Intent intent = new Intent(ClassMenuActivity.this, TranslateActivity.class);
                    intent.putExtra(TranslateActivity.FILE_NAME, adapter.getItem(position).getValue(Model.SLUG) + ".srt");
                    intent.putExtra(TranslateActivity.FILE_PATH, Config.ResourceFilePathRoot + adapter.getItem(position).getValue(Resource.PROJECT) + "/");
                    startActivity(intent);
                }
            }
        });
    }

    public void scanFile(String folder) {
        File file = FileUtils.getFile(folder, "", this);
        HashMap<String, String> files = FileScan.getFileListOnSys(file);
    }

    public static void StorageStatus(Activity activity) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(activity, R.string.sdcard_readonly, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            Toast.makeText(activity, R.string.sdcard_shared, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(activity, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    public void getProjectDetails(String slug) {
        txRestClientUsage.getProjectDetails(slug);
    }

    {
//        File file = FileUtils.initFolder(FileUtils.rootFolderName);
//        HashMap<String, String> files = FileScan.getFileListOnSys(file);
//        projectList = new ArrayList<String>();
//
//        for (String s : files.keySet()) {
//            projectList.add(s);
//            Log.i(TAG, "file " + s);
//        }
    }

    public JsonHttpResponseHandler projectHandler = new JsonHttpResponseHandler();
}
