package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Project;
import com.eduinfinity.dimu.translatehelper.utils.Config;
import com.eduinfinity.dimu.translatehelper.utils.JsonUtils;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

public class StartActivity extends Activity {
    final static String[] mottoes = {"勿忘初心，方得始终", "打破教育边界", "Just for fun"};
    private static final String VERSION_KEY = "VERSION_KEY";
    private static final String TAG = StartActivity.class.getName();
    private TextView tv;
    private Center center = Center.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        tv = (TextView) findViewById(R.id.textView_motto);
        //从assert中获取有资源，获得app的assert，采用getAserts()，通过给出在assert/下面的相对路径。在实际使用中，字体库可能存在于SD卡上，可以采用createFromFile()来替代createFromAsset。
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/wawasc.otf");
        tv.setTypeface(face);
        int i = (int) Math.floor(Math.random() * mottoes.length);
        tv.setText(mottoes[i]);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, LessonMenuActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        List<Model> projectList = center.getProjectList();
        projectList.clear();
        center.setContext(this.getApplicationContext());
        JsonUtils.parseProjectList("/", Config.ProjectConfig, projectList);
        if (projectList.size() > 0) {
            Project project = (Project) projectList.get(0);
            center.setCurrentProject(project);
            Log.i("START", "" + projectList.size());
        }

        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);

        // The following exceptions will be blacklisted, i.e.: When an exception
        // of this type is raised, the request will not be retried and it will
        // fail immediately.
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);

        new Handler().postDelayed(r, 2000);// 2秒后关闭，并跳转到主页面
    }

    public boolean firstLoad() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int currentVersion = info.versionCode;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = prefs.getInt(VERSION_KEY, 0);
            if (currentVersion > lastVersion) {
                //如果当前版本大于上次版本，该版本属于第一次启动
                //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
                prefs.edit().putInt(VERSION_KEY, currentVersion).commit();
                Log.i(TAG, "first load");
//                Intent intent = new Intent(this, .class);
//                startActivity(intent);
                return true;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "first load " + e.toString());
            return true;
        }
    }

    public boolean getPassWord() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String id = prefs.getString(Config.ID, "");
        String password = prefs.getString(Config.PASSWORD, "");
        center.setIDAndPassWord(id, password);
        return true;
    }
}
