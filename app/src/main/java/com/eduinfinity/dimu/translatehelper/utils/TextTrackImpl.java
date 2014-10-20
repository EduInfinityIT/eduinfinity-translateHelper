package com.eduinfinity.dimu.translatehelper.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Dimu on 10/20/14.
 */
public class TextTrackImpl {
    private List<Line> subs;

    public TextTrackImpl() {
        subs = new ArrayList<Line>();
    }

    public List<Line> getSubs() {
        return subs;
    }

    public static class Line {
        private long startTime, endTime;
        private String lineString;

        public Line(long startTime, long endTime, String lineString) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.lineString = lineString;
        }
    }
}
