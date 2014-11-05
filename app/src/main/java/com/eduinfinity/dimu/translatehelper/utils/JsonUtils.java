package com.eduinfinity.dimu.translatehelper.utils;


import android.app.Activity;
import android.util.Log;

import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class JsonUtils {
    private static final String TAG = "JsonUtils";
//    private static final String COURSE = "course.config";


    public static synchronized boolean writeJson(String path, String name, List<Model> modelList, Activity activity) {
        String allString = "";
        try {
            JSONArray courseArray = new JSONArray();

            for (Model p : modelList) {
                JSONObject pItem = new JSONObject();
                for (int i = 0; i < Model.KEYS.length; i++) {
                    String k = Model.KEYS[i];
                    String value = p.getValue(k);
                    if (value != null) pItem.put(k, value);
                }
                pItem.put(Model.STATUS, p.getStatus());
                courseArray.put(pItem);
            }
            allString = courseArray.toString();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "create  " + name + " err " + e);
            return false;
        }
        return FileUtils.writeFileOUTStorage(path, name, allString, activity);
    }

    public static List<Model> parseProjectList(String path, String fileName, List<Model> projects, Activity activity) {
        String allString = "";
        projects.clear();
        allString = FileUtils.readFileOutStorage(path, fileName, activity);
        if (allString == null || allString.equals("")) return projects;
        try {
            JSONArray jsonArray = new JSONArray(allString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject modelItem = jsonArray.getJSONObject(i);
                Project project = new Project();
                for (int j = 0; j < Project.KEYS.length; j++) {
                    String s = Project.KEYS[j];
                    try {
                        project.putValue(s, modelItem.getString(s));
                    } catch (JSONException e) {
                        Log.i(TAG, "parse json err " + e);
                    }
                }
                project.setStatus(modelItem.getInt(Model.STATUS));
                projects.add(project);
            }
        } catch (JSONException e) {
            Log.i(TAG, "parse json err " + e);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return projects;
    }

    public static List<Model> parseResourceList(String path, String fileName, List<Model> resources, Activity activity) {
        String allString = "";
        resources.clear();
        allString = FileUtils.readFileOutStorage(path, fileName, activity);
        if (allString == null || allString.equals("")) return resources;
        resources = parseResourceList(allString, resources);
        return resources;
    }

    private static List<Model> parseResourceList(String allString, List<Model> resources) {
        try {
            JSONArray jsonArray = new JSONArray(allString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject modelItem = jsonArray.getJSONObject(i);
                Resource resource = new Resource();
                for (int j = 0; j < Resource.KEYS.length; j++) {
                    String s = Resource.KEYS[j];
                    try {
                        resource.putValue(s, modelItem.getString(s));
                    } catch (JSONException e) {
                        Log.i(TAG, "parse json err " + e);
                    }
                }
                resource.setStatus(modelItem.getInt(Model.STATUS));
                resources.add(resource);
            }
        } catch (JSONException e) {
            Log.i(TAG, "parse json err " + e);
        }
        return resources;
    }

}
