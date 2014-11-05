package com.eduinfinity.dimu.translatehelper.utils;

import com.eduinfinity.dimu.translatehelper.adapter.model.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Dimu on 10/30/14.
 */
public class CollectionUtils {
    public static List<Model> map2list(Map<String, Model> map) {
        List<Model> list = new ArrayList<Model>();
        Collection<Model> collection = map.values();
        for (Model o : collection) {
            list.add(o);
        }
        return list;
    }
}
