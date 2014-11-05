package com.eduinfinity.dimu.translatehelper.adapter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dimu on 10/23/14.
 */
public class Resource implements Model {
    private Map<String, String> content = new HashMap<String, String>();

    public static final String CONTENT = "content";
    public static final String PROJECT = "project";
//    public static final String TRANSLATE = "translate";

    private int currentStatus = Model.INIT;

    public Resource() {
    }

    @Override
    public int getStatus() {
        return currentStatus;
    }

    @Override
    public synchronized void setStatus(int t) {
        currentStatus = t;
    }


    @Override
    public String[] getKeys() {
        return KEYS;
    }

    public Map<String, String> getContent() {
        return content;
    }
//
//    public void setContent(Map<String, String> content) {
//        this.content = content;
//    }

    public void putValue(String key, String value) {
        content.put(key, value);
    }

    public String getValue(String key) {
        return content.get(key);
    }


}
