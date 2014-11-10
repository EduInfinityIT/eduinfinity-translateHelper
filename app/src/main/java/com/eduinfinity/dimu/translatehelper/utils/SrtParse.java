package com.eduinfinity.dimu.translatehelper.utils;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

/**
 * Created by Dimu on 10/20/14.
 */
public class SrtParse {
    private static final String TAG = "SrtParse";

    public static TextTrackImpl parse(InputStream is, TextTrackImpl track) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
//        TextTrackImpl track = new TextTrackImpl();
        String numberString;
        while ((numberString = r.readLine()) != null && !numberString.trim().equals("")) {
            int index = Integer.parseInt(numberString);
            String timeString = r.readLine();
            String lineString = "";
            String s;
            while (!((s = r.readLine()) == null || s.trim().equals(""))) {
                lineString += s + "\n";
            }
            long startTime = parse(timeString.split("-->")[0]);
            long endTime = parse(timeString.split("-->")[1]);

            track.getSubs().add(new TextTrackImpl.Line(index, startTime, endTime, lineString));
        }
        return track;
    }

    private static long parse(String in) {
        long hours = Long.parseLong(in.split(":")[0].trim());
        long minutes = Long.parseLong(in.split(":")[1].trim());
        long seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim());
        long millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());

        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;

    }

    public static String convertTime2String(long t) {
        int h = (int) (t / (60 * 60 * 1000));
        int m = (int) ((t % (60 * 60 * 1000)) / (60 * 1000));
        int s = (int) (t % (60 * 1000) / 1000);
        int mm = (int) (t % 1000);
        return "" + h + ":" + m + ":" + s + "," + mm;
    }

    public static String convertSrt2String(List<TextTrackImpl.Line> list) {
        String allString = "";
        for (TextTrackImpl.Line line : list) {
            allString = allString + line.index + "\n";
            allString = allString + convertTime2String(line.startTime) + "-->" + convertTime2String(line.endTime) + "\n";
            allString = allString + line.trans + "\n";
            allString = allString + "\n";
        }
        return allString;
    }

    public static TextTrackImpl parseTrans(FileInputStream fis, TextTrackImpl track) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(fis, "UTF-8"));
        String numberString;
        while ((numberString = r.readLine()) != null && !numberString.equals("")) {
            int index = Integer.parseInt(numberString);
            String timeString = r.readLine();
            String lineString = "";
            String s;
            while (!((s = r.readLine()) == null || s.trim().trim().equals(""))) {
                lineString += s + "\n";
            }
            long startTime = parse(timeString.split("-->")[0]);
            long endTime = parse(timeString.split("-->")[1]);
            TextTrackImpl.Line line = track.getSubs().get(index - 1);
            if (line.startTime == startTime && line.endTime == endTime) {
                track.getSubs().get(index - 1).trans = lineString;
            } else {
                Log.w(TAG, "parse trans error at" + index);
                Log.e(TAG, "s1" + line.startTime + " s2 " + startTime);
                Log.e(TAG, "e1" + line.endTime + " e2 " + endTime);
            }
        }
        return track;
    }
}

