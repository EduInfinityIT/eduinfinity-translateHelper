package com.eduinfinity.dimu.translatehelper.adapter.model;

import java.util.Map;

public interface Model {
    public static final int INIT = 0;
    public static final int RES_DOWNED = 1;
    public static final int TRANS_DOWNED = 2;
    public static final int CHANGED = 3;
    public static final int UPLOADED = 4;
    public static final int typeCount = 5;
    public static int[] statusColors = {0xddcb5050, 0xdde1776f, 0xdde5e293, 0xdd78abf2, 0xdd5cc45f};

    public static final String NAME = "name";
    public static final String SLUG = "slug";
    public static final String VIDEO = "VIDEO";
    public static final String[] KEYS = {SLUG, NAME, VIDEO};
    public static final String STATUS = "status";

    public int getStatus();

    public void setStatus(int t);

    public String[] getHTTPKeys();

    public String[] getSaveKeys();

    public Map<String, String> getContent();

//    public void setContent(Map<String, String> content);

    public void putValue(String key, String value);


    public String getValue(String key);
}
