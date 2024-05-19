package com.feng.player.db

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File

data class Item(val name: String, val path: String)

fun getLocalFiles(context: Context): List<Item> {
    val files = mutableListOf<File>()

    // 合并文件系统和MediaStore的查询逻辑
    val filesAndMedia = Environment.getExternalStorageDirectory().listFiles { _, name -> name.endsWith(".mp4") }?.toMutableList() ?: mutableListOf()

    // 查询MediaStore中的视频
    val audioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(audioUri, arrayOf(MediaStore.Video.VideoColumns.DATA), null, null, null)

    if (cursor != null) {
        try {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                // 过滤掉非.mp4文件
                if (path.endsWith(".mp4")) {
                    val file = File(path)
                    filesAndMedia.add(file)
                }
            }
        } finally {
            cursor.close()
        }
    }

    // 处理所有找到的文件
    files.addAll(filesAndMedia)

    return filesAndMedia.map { Item(it.name, it.absolutePath) }.toMutableList()
}