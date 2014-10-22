package com.eduinfinity.dimu.translatehelper.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.eduinfinity.dimu.translatehelper.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dimu on 10/20/14.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";
    public static final String EduInfinityName = "/EduInfinity/";


    public static TextTrackImpl readFile(String fileName, Activity activity) {
        String allString = "";
        File file = null;
        try {
            String filePath = Environment.getExternalStorageDirectory().toString() + EduInfinityName + fileName;
            file = new File(filePath);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
            // TODO: handle exception
        }
        TextTrackImpl textTrackImpl = new TextTrackImpl();
        try {
            FileInputStream fis = new FileInputStream(file);
            textTrackImpl = SrtParse.parse(fis);
            fis.close();
            Log.i(TAG, "read " + fileName + " success" + allString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);

        }
        return textTrackImpl;
    }

    public static TextTrackImpl demo(Activity activity) {
        TextTrackImpl textTrackImpl = new TextTrackImpl();
        try {
            InputStream inputStream = activity.getResources().openRawResource(R.raw.demo);
            textTrackImpl = SrtParse.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + "parse err " + e);
        }
        return textTrackImpl;
    }

    public static boolean isInitFolder(String path) {
        File newDir = new File(Environment.getExternalStorageDirectory().toString() + path);
        Log.i(TAG, "newDir  " + newDir.getPath());
        if (!newDir.exists()) {
            newDir.mkdirs();
            return false;
        }
        return true;
    }

    public static File initFolder(String path) {
        File newDir = new File(Environment.getExternalStorageDirectory().toString() + path);
        Log.i(TAG, "newDir  " + newDir.getPath());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return newDir;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
