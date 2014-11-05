package com.eduinfinity.dimu.translatehelper.adapter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dimu on 10/23/14.
 */
public class Project implements Model {
    private Map<String, String> content = new HashMap<String, String>();
    private Map<String, Resource> resourceMap = new HashMap<String, Resource>();
    public static final String RESOURCE = "resources";
    //    public static final String LAST_UPDATED = "last_updated";
//    public static final String SOURCE_LANGUAGE_CODE = "source_language_code";
//    public static final String LONG_DESCRIPTION = "long_description";
    public static final String[] KEYS = {SLUG, NAME, RESOURCE};
    private int currentStatus = Model.INIT;

    public Project() {
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

//    public void setContent(Map<String, String> content) {
//        this.content = content;
//    }

    public void putValue(String key, String value) {
        content.put(key, value);
    }

    public String getValue(String key) {
        return content.get(key);
    }

    public void putResource(String slug, Resource resource) {
        resourceMap.put(slug, resource);
    }

    public Map<String, Resource> getResourceMap() {
        return resourceMap;
    }

    public List<Model> getResourceList() {
        List<Model> list = new ArrayList<Model>();
        Collection<Resource> collection = resourceMap.values();
        for (Model o : collection) {
            list.add(o);
        }
        return list;
    }

    public Resource getResource(String slug) {
        return resourceMap.get(slug);
    }
}
