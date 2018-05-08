package com.zyl.mp3cutter.mp3cut.logic;

import com.zyl.mp3cutter.home.bean.MusicInfo;

import org.jaudiotagger.tag.FieldKey;
import org.junit.Test;

import java.io.File;

import static com.zyl.mp3cutter.common.constant.CommonConstant.RING_FORMAT;

/**
 * Description: 单元测试
 * Created by zouyulong on 2017/12/21.
 */
public class Mp3CutLogicTest {
    /**
     * 源mp3文件为可变比特率，文件生成的单元测试
     * @throws Exception
     */
    @Test
    public void generateMp3ByVBR() throws Exception {
        String targetMp3FilePath = "app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/new_vbr" + RING_FORMAT;
        long startTime = 20 * 1000; //剪切的开始时间（毫秒）
        long endTime = 120 * 1000; //剪切的结束时间（毫秒）
        Mp3CutLogic helper = new Mp3CutLogic(new File("app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/vbr.mp3"));
        helper.generateNewMp3ByTime(targetMp3FilePath, startTime, endTime);
    }

    /**
     * 源mp3文件为恒定比特率，文件生成的单元测试
     * @throws Exception
     */
    @Test
    public void generateNewMp3ByCBR() throws Exception {
        String targetMp3FilePath = "app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/new_cbr" + RING_FORMAT;
        long startTime = 68 * 1000; //剪切的开始时间（毫秒） （眼前的-阅读浩瀚的书海）
        long endTime = 216 * 1000; //剪切的结束时间（毫秒）
        Mp3CutLogic helper = new Mp3CutLogic(new File("app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/cbr.mp3"));
        helper.generateNewMp3ByTime(targetMp3FilePath, startTime, endTime);
    }

    @Test
    public void testGetMp3Info(){
        MusicInfo mp3Info = Mp3InfoUtils.readMp3Info("app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/cankuyueguang.mp3");
        if(mp3Info!=null)
            System.out.print(mp3Info.toString());
    }

    @Test
    public void testWriteMp3Info(){
        Mp3InfoUtils.writeMp3Info("app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/cankuyueguang.mp3",
                FieldKey.TITLE, "大龙");
    }

//    @Test
//    public void getFrameCount(){
//        String targetMp3FilePath = "app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/requestData" + RING_FORMAT;
//        long startTime = 20 * 1000; //剪切的开始时间（毫秒）
//        long endTime = 50 * 1000; //剪切的结束时间（毫秒）
//        Mp3CutLogic helper = new Mp3CutLogic(new File("app/src/requestData/java/com/zyl/mp3cutter/mp3cut/logic/vbr.mp3"));
//        helper.
//    }
}