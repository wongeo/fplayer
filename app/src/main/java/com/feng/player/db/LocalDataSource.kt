package com.feng.player.db

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File

data class Item(val name: String, val path: String)

fun getLocalFiles(context: Context): List<Item> {
    // 使用 HashSet 来去重
    val uniqueFiles = HashSet<File>()

    // 合并文件系统和MediaStore的查询逻辑
    addFilesFromExternalStorage(uniqueFiles)
    addVideosFromMediaStore(context, uniqueFiles)

    // 处理所有找到的文件
    return uniqueFiles.map { Item(it.name, it.absolutePath) }
}

// 从文件系统中添加.mp4文件
private fun addFilesFromExternalStorage(uniqueFiles: HashSet<File>) {
    val externalStorageDirectory = Environment.getExternalStorageDirectory()
    externalStorageDirectory.listFiles { _, name -> name.endsWith(".mp4") }?.forEach { file ->
        uniqueFiles.add(file)
    }
}

// 从MediaStore中添加视频文件
private fun addVideosFromMediaStore(context: Context, uniqueFiles: HashSet<File>) {
    val audioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(audioUri, arrayOf(MediaStore.Video.VideoColumns.DATA), null, null, null)
    cursor?.use {
        while (it.moveToNext()) {
            val path = it.getString(it.getColumnIndex(MediaStore.Video.Media.DATA))
            // 过滤掉非.mp4文件并去重
            if (path.endsWith(".mp4")) {
                val file = File(path)
                uniqueFiles.add(file)
            }
        }
    }
}