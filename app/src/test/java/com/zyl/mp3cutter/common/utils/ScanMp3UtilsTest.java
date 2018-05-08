package com.zyl.mp3cutter.common.utils;

import android.app.Application;
import android.util.Log;

import com.zyl.mp3cutter.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

/**
 * Description: com.zyl.mp3cutter.common.utils
 * Created by zouyulong on 2018/2/2.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE, constants = BuildConfig.class, sdk = 25)
public class ScanMp3UtilsTest {
//    TestActivity mainActivity;
    @Before
    public void setUp() throws Exception {
        //输出日志
        ShadowLog.stream = System.out;
//        mainActivity = Robolectric.setupActivity(TestActivity.class);
    }

    @Test
    public void testScan() {
        Log.d("requestData", "testScan");
        Application application = RuntimeEnvironment.application;
//        Mp3ScanUtils.scanMusic(application);
    }
}