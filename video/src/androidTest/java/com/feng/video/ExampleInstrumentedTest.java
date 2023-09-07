package com.feng.video;

import android.app.Instrumentation;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.feng.video.util.DiscoverNetIpUtil;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.feng.video.test", appContext.getPackageName());
    }

    @Test
    public void findIp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // 获取Instrumentation实例
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

        // 模拟和授予权限
        instrumentation.getUiAutomation().executeShellCommand("pm grant com.feng.fplayer android.permission.ACCESS_WIFI_STATE");

        // 调用需要权限的API
        String ip = DiscoverNetIpUtil.getIpAddress(appContext);
        assertNotNull(ip);
    }

}