package com.feng.video.adapter;

import com.alibaba.fastjson.annotation.JSONField;

public class Item {

    @JSONField(name = "name")
    private String mName;

    @JSONField(name = "url")
    private String mUri;

    public Item() {

    }

    public Item(String name, String uri) {
        mName = name;
        mUri = uri;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }
}
