package com.eduinfinity.dimu.translatehelper.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static String getFileRootPath() {
        return Environment.getExternalStorageDirectory().toString() + Config.rootFolderName;
    }

    public static TextTrackImpl readTrans2track(String path, String fileName, TextTrackImpl textTrackImpl) {
        String allString = "";
        try {
            FileInputStream fis = getInputStream(path, fileName);
            if (fis == null) return textTrackImpl;
            textTrackImpl = SrtParse.parseTrans(fis, textTrackImpl);
            fis.close();
            Log.i(TAG, "read " + fileName + " success" + allString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);
        }
        return textTrackImpl;
    }

    public static TextTrackImpl readRes2track(String path, String fileName, TextTrackImpl textTrackImpl) {
        String allString = "";
        try {
            FileInputStream fis = getInputStream(path, fileName);
            if (fis == null) return textTrackImpl;
            textTrackImpl = SrtParse.parseSource(fis, textTrackImpl);
            fis.close();
            Log.i(TAG, "read " + fileName + " success" + allString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);
        }
        return textTrackImpl;
    }

    public static FileInputStream getInputStream(String path, String fileName) {
        String allString = "";
        File file = getFile(path, fileName);
        if (file == null || !file.exists()) return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Log.i(TAG, "read " + fileName + " success" + allString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);
        }
        return fis;
    }


    public static File getFile(String path, String fileName) {
        File file;
        File newdir = new File(Environment.getExternalStorageDirectory().toString() + Config.rootFolderName + path);
        if (!newdir.exists()) {
            Log.e(TAG, "Directory not created");
            newdir.mkdirs();
        }
        file = new File(Environment.getExternalStorageDirectory().toString() + Config.rootFolderName + path, fileName);
        return file;
    }

    public static boolean isExist(String path, String name) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + Config.rootFolderName + path, name);
        return file.exists();
    }

    public static File isInitFolder(String relativePath) {
        File newDir = new File(Environment.getExternalStorageDirectory().toString() + relativePath);
        Log.i(TAG, "newDir  " + newDir.getPath());
        if (!newDir.exists()) {
            newDir.mkdirs();
            if (newDir.exists()) {
                return newDir;
            }
            return null;
        }
        return newDir;
    }

    public static boolean writeFileInStorage(String fileName, String allString, Context context) {
        Log.i(TAG, "save  " + fileName + "  " + allString);
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(allString.getBytes());
            fos.close();
            Log.i(TAG, "save  " + fileName + " success");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "save " + fileName + " IOError " + e);
            return false;
        } catch (Exception e) {
            Log.i(TAG, "save " + fileName + " Error " + e);
            return false;
        }
    }


    public static boolean writeFileOUTStorage(String path, String fileName, String allString) {
        boolean isOK = existSDcard() && isExternalStorageWritable();
        if (!isOK) return false;
        Log.i(TAG, "isOK");
        FileOutputStream fos = null;
        if (allString == null) return false;
//        File file = getFile(fileName, activity);
        File file = new File(getFileRootPath() + path, fileName);
        file.getParentFile().mkdirs();
        try {
            fos = new FileOutputStream(file);
            fos.write(allString.getBytes("UTF-8"));
            fos.close();
            Log.i(TAG, "write ok " + file.getPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "保存文件出错");
            return false;
        } finally {
            file = null;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFileOutStorage(String path, String fileName) {
        String allString = "";
        File file = getFile(path, fileName);
        if (file == null || !file.exists()) return allString;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int hasRead = 0;
            StringBuilder sb = new StringBuilder("");
            while ((hasRead = fis.read(buff)) > 0) {
                sb.append(new String(buff, 0, hasRead));
            }
            allString = sb.toString();
            fis.close();
            Log.i(TAG, "read " + fileName + " success" + allString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "read  " + fileName + "err " + e);
        }
        return allString;
    }

    public static boolean saveBitmap(String path, String fileName, Bitmap bitmap) {
        boolean isOK = existSDcard() && isExternalStorageWritable();
        if (!isOK) return false;
        Log.i(TAG, "isOK");
        FileOutputStream fOut = null;
        File file = getFile(path, fileName);
        Log.e(TAG, "file name  " + file.getPath());
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();// 刷入文件
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "保存图片文件出错");
            return false;
        } finally {
            file = null;
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static boolean existSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static Bitmap readBitmap(String fileName) {

        Bitmap bitmap = null;
        try {
            String filePath = getFileRootPath() + "/" + fileName + ".png";
            File file = new File(filePath);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            // TODO: handle exception
        }
        return bitmap;

    }

    public static void delBitmap(String fileName) {
        String filePath = getFileRootPath() + "/" + fileName + ".png";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void delResource(String pro, String res) {
        String filePath = getFileRootPath() + "/" + pro;
        File file1 = new File(filePath + Config.SourceFolder, res + "srt");
        File file2 = new File(filePath + Config.TransFolder, res + "srt");
        if (file1.exists()) {
            file1.delete();
        }
        if (file2.exists()) {
            file2.delete();
        }

    }

    public static String size2String(Long size) {
        final int gb = 1073741824;
        final int mb = 1048576;
        final int kb = 1024;
        return "" + size / mb + "MB";
    }
}
