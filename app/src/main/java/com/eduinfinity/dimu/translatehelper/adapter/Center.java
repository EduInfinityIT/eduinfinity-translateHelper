package com.eduinfinity.dimu.translatehelper.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.eduinfinity.dimu.translatehelper.activity.LessonMenuActivity;
import com.eduinfinity.dimu.translatehelper.activity.SetTXIDActivity;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.http.TXRestClient;
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
    //TODO need remove
    private String ID = "relaxgo";
    private String passWord = "126bhgn333";

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

    public Context getContext() {
        return classActivity.getApplicationContext();
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


    public void onEventAsync(Resource.Translate data) {
        Resource resource = data.resource;
        FileUtils.writeFileOUTStorage(resource.getTransPath(), resource.getValue(Resource.SLUG) + ".srt", resource.getValue(Resource.TRANSLATE), classActivity);
    }

    public void onEventAsync(Resource.Source data) {
        Resource resource = data.resource;
        FileUtils.writeFileOUTStorage(resource.getSourcePath(), resource.getValue(Resource.SLUG) + ".srt", resource.getValue(Resource.SOURCE), classActivity);
    }

    public void onEventMainThread(Resource resource) {
//        String proSlug = resource.getValue(Resource.PROJECT);
//        if (currentProject != null && currentProject.getValue(Project.SLUG).equals(proSlug)) {
//            int i = 0;
//            boolean isHave = false;
//            for (; i < resourceList.size(); i++) {
//                Resource res = (Resource) resourceList.get(i);
//                if (res.getValue(Model.SLUG).equals(resource.getValue(Model.SLUG))) {
//                    resourceList.set(i, resource);
//                    isHave = true;
//                    break;
//                }
//            }
//            if (!isHave) resourceList.add(resource);
//            resourceAdapter.notifyDataSetChanged();
//            Log.i(TAG, "addResource");
//        }
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

    public Project getProject(String projectSlug) {
        Project project = null;
        if (getCurrentProject().getValue(Model.SLUG).equals(projectSlug)) {
            project = getCurrentProject();
        } else {
            for (Model p : getProjectList()) {
                if (p.getValue(Model.SLUG).equals(projectSlug)) project = (Project) p;
            }
        }
        return project;
    }


    public void setIDAndPassWord(String ID, String passWord) {
        this.ID = ID;
        this.passWord = passWord;
        triggerHandler(ID,passWord,true);
    }

    public String getID() {
        return ID;
    }

    public String getPassWord() {
        return passWord;
    }


    public void requestUser(TXRestClient.UserHandler userHandler) {
        Intent intent = new Intent(getContext(), SetTXIDActivity.class);
        classActivity.startActivity(intent);
        this.userHandler = userHandler;
    }

    private TXRestClient.UserHandler userHandler;

    public void triggerHandler(String id, String pw, boolean isSuccess) {
        if (isSuccess) userHandler.getUserSuccess(id, pw);
        else userHandler.getUserFailed(id, pw);
    }

}
