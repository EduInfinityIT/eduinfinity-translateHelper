package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.ModelListAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.JsonUtils;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ProjectActivity extends Activity {
    private static final String TAG = "ProjectActivity";
    private Center center = Center.getInstance();
    private List<Model> projectList;
    private TextView textView_addCourse;
    private EditText editText_courseName;
    private EventBus eventBus = EventBus.getDefault();
    private TXRestClientUsage txRestClientUsage = new TXRestClientUsage();
    private ModelListAdapter projectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
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
                addCourse();
            }
        });
        editText_courseName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        addCourse();
                    }
                    return true;
                }
                return false;
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView_course_menu);
        projectAdapter = center.getProjectAdapter(this);
        listView.setAdapter(projectAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelListAdapter adapter = (ModelListAdapter) parent.getAdapter();
                Model model = adapter.getItem(position);
                adapter.up2first(position);
                center.setCurrentProject((Project) model);
                ProjectActivity.this.finish();
            }
        });
    }

    private void addCourse() {
        InputMethodManager imm = (InputMethodManager) editText_courseName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(editText_courseName.getApplicationWindowToken(), 0);
        String slug = editText_courseName.getText().toString().trim();
        txRestClientUsage.getProjectDetails(slug);
        Log.i(TAG, slug);
        editText_courseName.setText("");
        SuperActivityToast.create(ProjectActivity.this, "明白，开始搜索" + slug, SuperToast.Duration.LONG).show();
    }

    //-----------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        JsonUtils.writeJson("/", Config.ProjectConfig, projectList, this);
        super.onDestroy();
    }
}
