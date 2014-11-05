package com.eduinfinity.dimu.translatehelper.control;

import com.eduinfinity.dimu.translatehelper.adapter.model.Model;
import com.eduinfinity.dimu.translatehelper.adapter.model.Resource;
import com.eduinfinity.dimu.translatehelper.http.TXRestClientUsage;
import com.eduinfinity.dimu.translatehelper.utils.FileUtils;

/**
 * Created by Dimu on 11/4/14.
 */
public class LessonSwipeMenu {
    public static String[] statusNextString = {"下载原文", "下载翻译", "上传翻译", "上传翻译", "删除该项"};
    public static String[] statusBackString = {"删除该项", "重置原文", "重置翻译", "重置翻译", "重置翻译"};

    public static void swipeNext(Resource resource, int menuType) {
        String projectSlug = resource.getValue(Resource.PROJECT);
        String resourceSlug = resource.getValue(Resource.SLUG);
        switch (menuType) {
            case Model.INIT:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                break;
            case Model.RES_DOWNED:
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.TRANS_DOWNED:
                TXRestClientUsage.postTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.CHANGED:
                TXRestClientUsage.postTranslateContent(projectSlug, resourceSlug);
                break;
            case Model.UPLOADED:
                FileUtils.delResource(projectSlug, resourceSlug);
                break;
        }


    }

    public static void swipeBack(Resource resource, int menuType) {
        String projectSlug = resource.getValue(Resource.PROJECT);
        String resourceSlug = resource.getValue(Resource.SLUG);
        switch (menuType) {
            case Model.INIT:
                FileUtils.delResource(projectSlug, resourceSlug);
                break;
            case Model.RES_DOWNED:
                TXRestClientUsage.getResourceContent(projectSlug, resourceSlug);
                break;
            case Model.TRANS_DOWNED:
                TXRestClientUsage.getTranslateContent(projectSlug, resourceSlug);
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
