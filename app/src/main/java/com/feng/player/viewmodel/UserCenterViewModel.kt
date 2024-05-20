package com.feng.player.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UserCenterViewModel : ViewModel() {

    var enableInput by mutableStateOf(true)

    fun load(context: Context): String {
        return getIP(context)
    }

    fun submit(context: Context, ip: String) {
        setIP(context, ip)
    }
}

fun setIP(context: Context, ip: String) {
    context.getSharedPreferences("service_ip", Context.MODE_PRIVATE).edit().putString("ip", ip).apply()
}

fun getIP(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("service_ip", Context.MODE_PRIVATE)
    return sharedPreferences.getString("ip", "") ?: ""
}