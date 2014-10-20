package com.eduinfinity.dimu.translatehelper.utils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Dimu on 10/20/14.
 */
public class fileUtils {

    private static final String TAG = "fileUtils";
    private static final String EduInfinityName = "/EduInfinity/";

    public static TextTrackImpl readFile(String fileName, Activity activity) {
        String allString = "";
        File file = null;
        try {
            String filePath = Environment.getExternalStorageDirectory().toString() + EduInfinityName + fileName + ".srt";
            file = new File(filePath);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
            // TODO: handle exception
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            TextTrackImpl textTrackImpl = SrtParse.parse(fis);
            fis.close();
            Log.i(TAG, "read " + fileName + " success" + allString);
            return textTrackImpl;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);
            return null;
        }

    }
}
