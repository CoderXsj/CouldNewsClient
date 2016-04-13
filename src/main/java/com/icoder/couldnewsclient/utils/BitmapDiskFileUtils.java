package com.icoder.couldnewsclient.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.icoder.couldnewsclient.app.MyApplication;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 图片缓存使用类
 */
public class BitmapDiskFileUtils {
    static BitmapDiskFileUtils instance = new BitmapDiskFileUtils();
    private static DiskLruCache DiskLruCache;

    private BitmapDiskFileUtils(){
        Context context = MyApplication.getContext();
        try {
            DiskLruCache = DiskLruCache.open(getDiskCacheDir(context, "bitmap"), getAppVersion(context), 1, 1024 * 1024 * 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BitmapDiskFileUtils getInstance(){
        return instance;
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void writeBitmap(String url,Bitmap bitmap) {
        String key = hashKeyForDisk(url);
        DiskLruCache.Editor editor = null;
        try {
            editor = DiskLruCache.edit(key);
            if (editor != null) {
                OutputStream os = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
            }
            DiskLruCache.flush();
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap readBitmap(String url) {
        String key = hashKeyForDisk(url);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = DiskLruCache.get(key);
            if(null != snapshot)
                return BitmapFactory.decodeStream(snapshot.getInputStream(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
