package com.zyl.mp3cutter.mp3cut.logic;

import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;

import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FORMAT;

/**
 * Description: 单元测试
 * Created by zouyulong on 2017/12/21.
 */
public class Mp3CutLogicTest {
    @Test
    public void generateNewMp3ByTime() throws Exception {
        String targetMp3FilePath = "app/src/test/java/com/zyl/mp3cutter/mp3cut/logic/test" + RING_FORMAT;
        RandomAccessFile targetMp3File = null;
        try {
        targetMp3File = new RandomAccessFile(targetMp3FilePath, "rw");
        Mp3CutLogic helper = new Mp3CutLogic(new File("app/src/test/java/com/zyl/mp3cutter/mp3cut/logic/laozi.mp3"));
            helper.generateNewMp3ByTime(targetMp3File, 20*1000, 50*1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (targetMp3File != null)
                targetMp3File.close();
        }
    }

}