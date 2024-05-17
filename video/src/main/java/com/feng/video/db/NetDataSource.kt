package com.feng.video.db

import android.content.Context
import com.feng.video.adapter.Item
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NetDataSource {
    companion object {
        private val sHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build()

        @JvmStatic
        fun getItems(context: Context, address: String): List<Item>? {
            val url = "http://$address:3000"
            val request = Request.Builder()
                .url(url)
                .get() // 默认就是GET请求，可以不写
                .build()

            val call = sHttpClient.newCall(request)
            return try {
                val response = call.execute()
                val json = response.body()?.string()
                val jsonObject = JSONObject(json)
                val jsonArray = jsonObject.getJSONArray("data")
                val items = mutableListOf<Item>()
                for (i in 0 until jsonArray.length()) {
                    val itemJson = jsonArray.getJSONObject(i)
                    if (itemJson.getString("type") == "mp4") {
                        items.add(Item(itemJson.getString("name"), itemJson.getString("url")))
                    }
                }
                items
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}