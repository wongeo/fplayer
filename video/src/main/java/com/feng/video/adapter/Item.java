package com.feng.video.adapter;

public class Item {
    private String mName;
    private String mUri;

    public Item(String name, String uri) {
        mName = name;
        mUri = uri;
    }

    public String getName() {
        return mName;
    }

    public String getUri() {
        return mUri;
    }
}
