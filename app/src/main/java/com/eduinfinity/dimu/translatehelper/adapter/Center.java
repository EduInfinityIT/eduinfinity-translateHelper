package com.eduinfinity.dimu.translatehelper.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;
import com.eduinfinity.dimu.translatehelper.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Dimu on 10/23/14.
 */
public class Center {
    private static final String TAG = "CENTER";
    private static Center ourInstance = new Center();
    private List<Model> projectList = new ArrayList<Model>();
    private List<Model> resourceList = new ArrayList<Model>();
    private ModelListAdapter resourceAdapter;
    private ModelListAdapter projectAdapter;
    private Object resourceListAdapter;
    private Activity classActivity;
    private Project currentProject;
    private EventBus eventBus = EventBus.getDefault();
    private Context context;

    public static Center getInstance() {
        return ourInstance;
    }

    private Center() {
        eventBus.register(this);
    }

    public List<Model> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Model> projectList) {
        this.projectList = projectList;
    }

    public List<Model> getResourceList() {
        return resourceList;
    }

    public ModelListAdapter getProjectAdapter(Context context) {
        if (projectAdapter == null) projectAdapter = new ModelListAdapter(context, projectList);
        return projectAdapter;
    }

    public ModelListAdapter getResourceAdapter(Context context) {
        if (resourceAdapter == null) resourceAdapter = new ModelListAdapter(context, resourceList);
        return resourceAdapter;
    }

    public void setClassActivity(Activity classActivity) {
        this.classActivity = classActivity;
    }

    public Activity getContext() {
        return classActivity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public void onEventAsync(Resource resource) {
        String proSlug = resource.getValue(Resource.PROJECT);
        if (currentProject != null && currentProject.getValue(Project.SLUG).equals(proSlug)) {
            resourceList.add(resource);
            resourceAdapter.notifyDataSetChanged();
            Log.i(TAG, "addResource");
        }
        boolean result = FileUtils.writeFileOUTStorage("/" + resource.getValue(Resource.PROJECT), resource.getValue(Resource.SLUG) + ".srt", resource.getValue(Resource.CONTENT), classActivity);
        if (result) resource.setStatus(Model.RES_DOWNED);
    }

    public void onEventMainThread(Project project) {
        String slug = project.getValue(Model.SLUG);

        for (int i = 0; i < projectList.size(); i++) {
            Model model = projectList.get(i);
            if (model.getValue(Model.SLUG).equals(slug)) {
                return;
            }
        }
        projectList.add(project);
        projectAdapter.notifyDataSetChanged();
        JsonUtils.writeJson("/", Config.ProjectConfig, projectList, getContext());
        List<Model> list = project.getResourceList();
        JsonUtils.writeJson("/" + slug, Config.ResourceConfig, list, getContext());
    }
}
