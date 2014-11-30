package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.baoyz.swipemenulistview.*;
import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.ModelListAdapter;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.control.LessonSwipeMenu;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileScan;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.JsonUtils;
import de.greenrobot.event.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class LessonMenuActivity extends Activity {


    private static final String TAG = "LessonMenuActivity";
    public static final int USER = 1;
    private Center center = Center.getInstance();
    private List<Model> resourceList;
    private TextView textView_course_menu;
    private EventBus eventBus = EventBus.getDefault();
    private SwipeMenuListView listView;
    private ModelListAdapter adapter;

    private Project CurrentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_menu);
        StorageStatus(this);
        center.setClassActivity(this);
        init();
        eventBus.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        Intent intent;
        switch (id) {
            case R.id.help:
                intent = new Intent(this, HowToActivity.class);
                startActivity(intent);
                break;
            case R.id.setID:
                intent = new Intent(this, SetTXIDActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                Toast.makeText(this, "这是无边界字幕组翻译专用APP", Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void init() {
        textView_course_menu = (TextView) findViewById(R.id.textView_course_menu);
        textView_course_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LessonMenuActivity.this, ProjectActivity.class);
                startActivity(intent);
            }
        });

        adapter = center.getResourceAdapter(this);
        listView = (SwipeMenuListView) findViewById(R.id.listView_class_menu);
        loadListView(listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelListAdapter adapter = (ModelListAdapter) ((SwipeMenuAdapter) parent.getAdapter()).getWrappedAdapter();
                Resource model = (Resource) adapter.getItem(position);
                if (model.getStatus() < Model.RES_DOWNED) {
                    TXRestClientUsage.getResourceContent(model.getProjectSlug(), model.getValue(Model.SLUG));
                    return;
                }
                Intent intent = new Intent(LessonMenuActivity.this, TranslateActivity.class);
                intent.putExtra(TranslateActivity.ResourceName, model.getValue(Model.NAME));
                intent.putExtra(TranslateActivity.ResourceSlug, model.getValue(Model.SLUG));
                intent.putExtra(TranslateActivity.ProjectSlug, model.getProjectSlug());
                String path = model.getValue(Resource.VIDEO);
                if (path != null) intent.putExtra(TranslateActivity.VIDEO_PATH, path);
                intent.putExtra(TranslateActivity.STATUS, model.getStatus());
                startActivity(intent);
//                adapter.up2first(position);
                saveResourceList();

            }
//            }
        });
    }

    public void scanFile(String folder) {
        File file = FileUtils.getFile(folder, "");
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

    public void loadListView(SwipeMenuListView listView) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem nextItem = new SwipeMenuItem(getApplicationContext());
                nextItem.setWidth(200);
                nextItem.setTitleSize(10);
                nextItem.setTitleColor(Color.WHITE);

                SwipeMenuItem backItem = new SwipeMenuItem(getApplicationContext());
                backItem.setWidth(200);

                backItem.setTitleSize(10);
                backItem.setTitleColor(Color.WHITE);

//                backItem.setIcon(R.drawable.ic_delete);
                int menuType = menu.getViewType();
                int backColorID = menuType - 1;
                int upColorID = menuType + 1;
                if (backColorID == -1) backColorID = 0;
                if (upColorID == Model.statusColors.length)
                    upColorID = Model.statusColors.length - 1;
                nextItem.setBackground(new ColorDrawable(Model.statusColors[upColorID]));
                nextItem.setTitle(LessonSwipeMenu.statusNextString[menuType]);

                backItem.setBackground(new ColorDrawable(Model.statusColors[backColorID]));
                backItem.setTitle(LessonSwipeMenu.statusBackString[menuType]);

                menu.addMenuItem(nextItem);
                menu.addMenuItem(backItem);

            }
        };

        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ModelListAdapter adapter = center.getResourceAdapter(LessonMenuActivity.this);
                Resource resource = (Resource) adapter.getItem(position);
                switch (index) {
                    case 0:
                        LessonSwipeMenu.swipeNext(resource, menu.getViewType());
                        break;
                    case 1:
                        LessonSwipeMenu.swipeBack(resource, menu.getViewType());
                        break;
                }
                return false;
            }
        });
        listView.setCloseInterpolator(new BounceInterpolator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (center.isFirstLoad()) {
            Intent intent = new Intent(this, HowToActivity.class);
            startActivity(intent);
            center.setIsFirstLoad(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        resourceList = center.getResourceList();
        Project project = CurrentProject = center.getCurrentProject();

        if (project == null) return;

        String projectSlug = project.getValue(Model.SLUG);
        if (project.getResourceMap().size() == 0) {
            Log.i(TAG, "load project " + projectSlug);
            if (!FileUtils.isExist("/" + projectSlug, Config.ResourceConfig)) {
                TXRestClientUsage.getProjectDetails(projectSlug);
                return;
            }
            JsonUtils.parseResourceList("/" + projectSlug, Config.ResourceConfig, resourceList);
            for (Model model : resourceList) {
                project.putResource(model.getValue(Model.SLUG), (Resource) model);
            }
        } else {
            Log.i(TAG, "load project " + projectSlug);
            resourceList.clear();
            for (Resource r : project.getResourceMap().values()) {
                resourceList.add(r);
            }
        }
        center.getResourceAdapter(this).notifyDataSetChanged();
    }

    private void saveResourceList() {
        if (CurrentProject != null)
            JsonUtils.writeJson("/" + CurrentProject.getValue(Model.SLUG), Config.ResourceConfig, resourceList, this);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        saveResourceList();
        super.onPause();
    }

    public void onEventMainThread(Resource resource) {
        Log.e(TAG, adapter.toString());
        adapter.notifyDataSetChanged();
    }
}
