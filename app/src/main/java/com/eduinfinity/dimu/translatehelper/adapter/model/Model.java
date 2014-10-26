package com.eduinfinity.dimu.translatehelper.adapter.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dimu on 10/23/14.
 */
public interface Model {
    public static final int NOT_INIT = 0;
    public static final int INIT = 1;
    public static final int NOT_UPDATED = 2;
    public static final int UPDATING = 3;
    public static final int UPDATED = 4;
    public static final String NAME = "name";
    public static final String SLUG = "slug";

    public int getStatus();

    public void setStatus(int t);

    public String[] getKeys();

    public Map<String, String> getContent();

//    public void setContent(Map<String, String> content);

    public void putValue(String key, String value);

    public String getValue(String key);
}
