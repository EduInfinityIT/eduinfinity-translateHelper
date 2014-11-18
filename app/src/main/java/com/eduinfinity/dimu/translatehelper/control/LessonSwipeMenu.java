package com.eduinfinity.dimu.translatehelper.control;

import android.util.Log;
import android.widget.Toast;

import com.eduinfinity.dimu.translatehelper.adapter.Center;
import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;

/**
 * Created by Dimu on 11/4/14.
 */
public class LessonSwipeMenu {
    private static final String TAG = "LessonSwipeMenu";
    public static String[] statusNextString = {"下载原文", "下载翻译", "上传翻译", "上传翻译", "删除该项"};
    public static String[] statusBackString = {"删除该项", "下载原文", "下载原文", "下载翻译", "下载翻译"};

    public static void swipeNext(Resource resource, int menuType) {
        String projectSlug = resource.getProjectSlug();
        String resourceSlug = resource.getValue(Resource.SLUG);
        Log.i(TAG, " " + menuType + " " + projectSlug + " " + resourceSlug);
        switch (menuType) {
            case Model.INIT:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                break;
            case Model.RES_DOWNED:
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.TRANS_DOWNED:
                Toast.makeText(Center.getInstance().getContext(),"功能待做",Toast.LENGTH_SHORT).show();
//                TXRestClientUsage.postTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.CHANGED:
                Toast.makeText(Center.getInstance().getContext(),"功能待做",Toast.LENGTH_SHORT).show();
//                TXRestClientUsage.postTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.UPLOADED:
                FileUtils.delResource(projectSlug, resourceSlug);
                break;
        }
    }

    public static void swipeBack(Resource resource, int menuType) {
        String projectSlug = resource.getProjectSlug();
        String resourceSlug = resource.getValue(Resource.SLUG);
        Log.i(TAG, " " + menuType + " " + projectSlug + " " + resourceSlug);
        switch (menuType) {
            case Model.INIT:
                FileUtils.delResource(projectSlug, resourceSlug);
                break;
            case Model.RES_DOWNED:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                break;
            case Model.TRANS_DOWNED:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                break;
            case Model.CHANGED:
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.UPLOADED:
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
                break;
        }

    }


}
