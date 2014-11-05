package com.eduinfinity.dimu.translatehelper.http;

import android.util.Log;
import android.widget.Toast;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Dimu on 10/23/14.
 */
public class TXRestClientUsage {
    public static final String TAG = "TXRestClientUsage";
    private static Center center = Center.getInstance();
    private static EventBus eventBus = EventBus.getDefault();

    public static void getProjectList(String slug) throws JSONException {
        TXRestClient.get(slug, null, new myJsonHttpResHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i(TAG, response.toString());
            }
        });
    }

    public static void getProjectDetails(final String slug) {
        TXRestClient.get(slug + "/?details", null, new myJsonHttpResHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i(TAG, response.toString());
                        Project project = new Project();
                        project.setStatus(Model.RES_DOWNED);
                        for (int i = 0; i < Project.KEYS.length; i++) {
                            try {
                                project.putValue(Project.KEYS[i], response.getString(Project.KEYS[i]));
                                if (Project.KEYS[i].equals(Project.RESOURCE)) {
                                    JSONArray resArray = response.getJSONArray(Project.RESOURCE);
                                    for (int j = 0; j < resArray.length(); j++) {
                                        try {
                                            JSONObject resItem = resArray.getJSONObject(j);
                                            Resource resource = new Resource();
                                            resource.putValue(Resource.SLUG, resItem.getString(Resource.SLUG));
                                            resource.putValue(Resource.NAME, resItem.getString(Resource.NAME));
                                            resource.putValue(Resource.PROJECT, slug);
                                            resource.setStatus(Model.INIT);
                                            project.putResource(resItem.getString(Resource.SLUG), resource);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e(TAG, e.toString());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        project.setStatus(Model.RES_DOWNED);
                        eventBus.post(project);
                    }
                }

        );
    }

    public static void getResourceContent(final String projectSlug, final String resourceSlug) {
        TXRestClient.get(projectSlug + "/resource/" + resourceSlug + "/content", null, new myJsonHttpResHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, headers.toString());
                Log.i(TAG, "success res" + projectSlug + "  " + resourceSlug);
                Project project = null;
                if (center.getCurrentProject().getValue(Model.SLUG).equals(projectSlug)) {
                    project = center.getCurrentProject();
                } else {
                    for (Model p : center.getProjectList()) {
                        if (p.getValue(Model.SLUG).equals(projectSlug)) project = (Project) p;
                    }
                }
                Resource resource = project.getResource(resourceSlug);
                try {
                    resource.putValue(Resource.CONTENT, response.getString(Resource.CONTENT));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                resource.setStatus(Model.RES_DOWNED);
                Center center = Center.getInstance();
                List<Model> projectList = center.getProjectList();
                for (Model pro : projectList) {
                    if (pro.getValue(Project.SLUG).equals(projectSlug))
                        ((Project) pro).putResource(resourceSlug, resource);
                }
                eventBus.post(resource);

            }
        });
    }

    public static void getTranslateContent(final String projectSlug, final String resourceSlug) {
        TXRestClient.get(projectSlug + "/resource/" + resourceSlug + "/translation/zh/", null, new myJsonHttpResHandler() {
            //https://www.transifex.com/api/2/project/coursera-ml/resource/93/translation/zh/
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, "success  trans" + projectSlug + "  " + resourceSlug);
                Project project = null;
                if (center.getCurrentProject().getValue(Model.SLUG).equals(projectSlug)) {
                    project = center.getCurrentProject();
                } else {
                    for (Model p : center.getProjectList()) {
                        if (p.getValue(Model.SLUG).equals(projectSlug)) project = (Project) p;
                    }
                }
            }
        });
    }

    public static void postTranslateContent(final String projectSlug, final String resourceSlug) {
        TXRestClient.post("", null, new myJsonHttpResHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }
        });
    }

    static class myJsonHttpResHandler extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Log.i(TAG, responseString);
            Toast.makeText(Center.getInstance().getContext(), R.string.resposeNotdone, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.i(TAG, response.toString());
            Toast.makeText(Center.getInstance().getContext(), R.string.resposeNotdone, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i(TAG, errorResponse.toString());
            Toast.makeText(Center.getInstance().getContext(), R.string.resquestResourceFailure, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            Log.i(TAG, errorResponse.toString());
            Toast.makeText(Center.getInstance().getContext(), R.string.resquestResourceFailure, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i(TAG, responseString);
            Toast.makeText(Center.getInstance().getContext(), R.string.resquestResourceFailure, Toast.LENGTH_LONG).show();
        }
    }
}
