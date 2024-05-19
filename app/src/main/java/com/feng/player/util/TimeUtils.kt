package com.feng.player.util

fun formatTime(milliseconds: Long): String {
    val hours = milliseconds / (60 * 60 * 1000)
    val minutes = (milliseconds - hours * 60 * 60 * 1000) / (60 * 1000)
    val seconds = (milliseconds - hours * 60 * 60 * 1000 - minutes * 60 * 1000) / 1000

    var res = "%02d:%02d".format(minutes, seconds)
    if (hours > 0) {
        res = "${"%02d".format(hours)}:$res"
    }
    // 使用现代Kotlin特性来简化字符串拼接和条件判断
    return res
}