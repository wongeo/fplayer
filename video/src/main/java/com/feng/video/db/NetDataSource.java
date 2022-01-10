package com.feng.video.db;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feng.video.adapter.Item;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetDataSource {

    public static List<Item> getItems(Context context) {
        String url = "http://30.77.78.102:8080/list";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String json = response.body().string();
            JSONObject jsonObject = JSON.parseObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                String name = jsonItem.getString("name");
                String uri = jsonItem.getString("url");
                Item item = new Item(name, uri);
                items.add(item);
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
