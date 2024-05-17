package com.feng.video.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.feng.video.adapter.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 本地数据源
 */
public class LocalDataSource {

    public static List<Item> getLocalFiles(Context context) {
        final List<File> files = new ArrayList<>();

        //读取存储卡跟目录mp4视频
        File dir = Environment.getExternalStorageDirectory();
        File[] files1 = dir.listFiles((dir1, name) -> name.endsWith("mp4"));

        Collections.addAll(files, files1);
        //读取视频文件
        Uri audioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(audioUri, new String[]{MediaStore.Video.VideoColumns.DATA}, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                int spiltIndex = path.indexOf(".");
                if (spiltIndex != -1) {
                    File file = new File(path);
                    files.add(file);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        List<Item> items = new ArrayList<>();
        for (File file : files) {
            items.add(new Item(file.getName(), file.getAbsolutePath()));
        }
        return items;
    }
}
