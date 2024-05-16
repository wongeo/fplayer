package com.feng.video.db;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feng.video.adapter.Item;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetDataSource {
    private static final OkHttpClient sHttpClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .build();
    public static List<Item> getItems(Context context, String address) {
        String url = "http://" + address + ":3000";
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = sHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String json = response.body().string();
            JSONObject jsonObject = JSON.parseObject(json);
            List<Item> items = JSON.parseArray(jsonObject.getString("data"), Item.class);
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
