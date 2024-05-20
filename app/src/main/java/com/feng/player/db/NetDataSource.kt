package com.feng.player.db

import android.content.Context
import com.feng.player.viewmodel.getIP
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private val sHttpClient: OkHttpClient = OkHttpClient.Builder()
    .readTimeout(3, TimeUnit.SECONDS)
    .connectTimeout(3, TimeUnit.SECONDS)
    .build()

fun getDataFromNet(context: Context): List<Item>? {
    return try {
        val address = getIP(context)
        val url = "http://$address:3000"
        val request = Request.Builder()
            .url(url)
            .build()

        val call = sHttpClient.newCall(request)
        val response = call.execute()
        val json = response.body()?.string()
        val jsonObject = json?.let { JSONObject(it) }
        val jsonArray = jsonObject?.getJSONArray("data")
        val items = mutableListOf<Item>()
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val itemJson = jsonArray.getJSONObject(i)
                if (itemJson.getString("type") == "mp4") {
                    items.add(Item(itemJson.getString("name"), itemJson.getString("url")))
                }
            }
        }
        items
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}