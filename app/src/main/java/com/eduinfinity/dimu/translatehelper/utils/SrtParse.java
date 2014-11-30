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

    public static TextTrackImpl parseSource(InputStream is, TextTrackImpl track) throws IOException {
        return parse(is, track, false);
    }

    public static TextTrackImpl parseTrans(InputStream fis, TextTrackImpl track) throws IOException {
        return parse(fis, track, true);
    }

    private static long parseTime(String in) {
        long hours = Long.parseLong(in.split(":")[0].trim());
        long minutes = Long.parseLong(in.split(":")[1].trim());
        long seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim());
        long millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());
        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;

    }

    public static String convertTime2String(long time) {
        int mileSeconds = (int) (time % 1000);
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, mileSeconds);
    }

    public static String convertSrt2String(List<TextTrackImpl.Line> list) {
        String allString = "";
        for (TextTrackImpl.Line line : list) {
            allString = allString + line.index + "\n";
            allString = allString + convertTime2String(line.startTime) + " --> " + convertTime2String(line.endTime) + "\n";
            allString = allString + line.trans + "\n\n";
        }
        return allString;
    }

    public static TextTrackImpl parse(InputStream fis, TextTrackImpl track, boolean isAddTrans) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(fis, "UTF-8"));
        String numberString;
        while ((numberString = r.readLine()) != null) {
            while (numberString.equals("")) {
                numberString = r.readLine();
                if (numberString == null) return track;
            }
            int index = Integer.parseInt(numberString);
            String timeString = r.readLine();
            String lineString = "";
            String s;
            while (!((s = r.readLine()) == null || s.trim().equals(""))) {
                lineString += s + "\n";
            }
            long startTime = parseTime(timeString.split("-->")[0]);
            long endTime = parseTime(timeString.split("-->")[1]);
            if (isAddTrans) {
                if (index > track.getSubs().size() || track.getSubs().size() == 0) return track;
                TextTrackImpl.Line line = track.getSubs().get(index - 1);

                if (line.startTime == startTime && line.endTime == endTime) {
                    track.getSubs().get(index - 1).trans = lineString;
                } else {
                    Log.w(TAG, "parse trans error at" + index);
                    Log.e(TAG, "s1" + line.startTime + " s2 " + startTime);
                    Log.e(TAG, "e1" + line.endTime + " e2 " + endTime);
                }
            } else {
                track.getSubs().add(new TextTrackImpl.Line(index, startTime, endTime, lineString.trim()));
            }

        }
        return track;
    }

}

