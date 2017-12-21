package com.zyl.mp3cutter.mp3cut.logic;

import org.junit.Test;

import java.io.File;

import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FORMAT;

/**
 * Description: 单元测试
 * Created by zouyulong on 2017/12/21.
 */
public class Mp3CutLogicTest {
    @Test
    public void generateNewMp3ByTime() throws Exception {
        String targetMp3FilePath = "app/src/test/java/com/zyl/mp3cutter/mp3cut/logic/test" + RING_FORMAT;
        long startTime = 20 * 1000; //剪切的开始时间（毫秒）
        long endTime = 50 * 1000; //剪切的结束时间（毫秒）
        Mp3CutLogic helper = new Mp3CutLogic(new File("app/src/test/java/com/zyl/mp3cutter/mp3cut/logic/laozi.mp3"));
        helper.generateNewMp3ByTime(targetMp3FilePath, startTime, endTime);
    }
}