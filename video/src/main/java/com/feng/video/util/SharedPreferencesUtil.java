package com.feng.video.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    public static final String mTAG = "SharedPreferencesUtil";

    // 创建一个写入器
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SharedPreferencesUtil mSharedPreferencesUtil;

    // 构造方法
    private SharedPreferencesUtil(Context context) {
        mPreferences = context.getSharedPreferences(mTAG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    // 单例模式
    public static SharedPreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesUtil == null) {
            mSharedPreferencesUtil = new SharedPreferencesUtil(context.getApplicationContext());
        }
        return mSharedPreferencesUtil;
    }

    // 存入数据
    public void put(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    // 获取数据
    public String get(String key) {
        return mPreferences.getString(key, "");
    }

    // 移除数据
    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }
}
