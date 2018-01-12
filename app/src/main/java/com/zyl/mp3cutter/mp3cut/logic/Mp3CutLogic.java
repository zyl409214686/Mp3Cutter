package com.zyl.mp3cutter.mp3cut.logic;

import com.zyl.mp3cutter.common.utils.FileUtils;
import com.zyl.mp3cutter.mp3cut.bean.Mp3Info;
import com.zyl.mp3cutter.mp3cut.util.StringUtil;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: mp3剪切逻辑
 * Created by zouyulong on 2017/12/1.
 * Person in charge :  zouyulong
 */
public class Mp3CutLogic {
    //缓存大小
    private static final int BUFFER_SIZE = 1024 * 1024;
    //mp3源文件
    private File mSourceMp3File;
    private Mp3Info mp3Info;

    public Mp3CutLogic(File mp3File) {
        this.mSourceMp3File = mp3File;
    }

    public File getmSourceMp3File() {
        return this.mSourceMp3File;
    }

    public void setmSourceMp3File(File mSourceMp3File) {
        this.mSourceMp3File = mSourceMp3File;
    }

    /**
     * 获取mp3信息
     *
     * @return
     */
    public Mp3Info getMp3Info() {
        if (this.mp3Info == null) {
            try {
                MP3File mp3 = new MP3File(this.mSourceMp3File);
                ID3v1Tag v1 = mp3.getID3v1Tag();
                String encoding = v1.getEncoding();
                MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
                this.mp3Info = new Mp3Info();
                this.mp3Info.setTitle(StringUtil.convertEncode(v1.getFirst(FieldKey.TITLE), encoding));
                this.mp3Info.setArtist(StringUtil.convertEncode(v1.getFirst(FieldKey.ARTIST), encoding));
                this.mp3Info.setAlbum(StringUtil.convertEncode(v1.getFirst(FieldKey.ALBUM), encoding));
                this.mp3Info.setTrackLength(header.getTrackLength());
                this.mp3Info.setBiteRate(header.getBitRate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.mp3Info;
    }

    /**
     * 根据时间生成新的mp3文件（分别支持VBR 和 CBR）
     *
     * @param targetFileStr 要生成的目标mp3文件
     * @param beginTime
     * @param endTime
     * @return
     */
    public void generateNewMp3ByTime(String targetFileStr, long beginTime, long endTime) throws Exception {
        MP3File mp3 = new MP3File(this.mSourceMp3File);
        MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
        if (header.isVariableBitRate()) {
            generateMp3ByTimeAndVBR(header, targetFileStr, beginTime, endTime);
        } else {
            generateMp3ByTimeAndCBR(header, targetFileStr, beginTime, endTime);
        }
    }

    /**
     * 根据时间和源文件生成MP3文件 （源文件mp3 比特率为vbr可变比特率）
     *
     * @param header
     * @param targetFileStr
     * @param beginTime
     * @param endTime
     * @throws IOException
     */
    private void generateMp3ByTimeAndVBR(MP3AudioHeader header, String targetFileStr, long beginTime, long endTime) throws IOException {
        long frameCount = header.getNumberOfFrames();
        int sampleRate = header.getSampleRateAsNumber();
        int sampleCount = 1152;//header.getNoOfSample();
        int paddingLength = header.isPadding() ? 1 : 0;
        //帧大小 = ( 每帧采样次数 × 比特率(bit/s) ÷ 8 ÷采样率) + Padding
        //getBitRateAsNumber 返回的为kbps 所以要*1000
        float frameSize = sampleCount * header.getBitRateAsNumber() / 8f / sampleRate * 1000 + paddingLength;
        //获取音轨时长
        int trackLengthMs = header.getTrackLength() * 1000;
        //开始时间与总时间的比值
        float beginRatio = (float) beginTime / (float) trackLengthMs;
        //结束时间与总时间的比值
        float endRatio = (float) endTime / (float) trackLengthMs;
        long startFrameSize = (long) (beginRatio * frameCount * frameSize);
        long endFrameSize = (long) (endRatio * frameCount * frameSize);
        //返回音乐数据的第一个字节
        long firstFrameByte = header.getMp3StartByte();
        generateTargetMp3File(targetFileStr, startFrameSize, endFrameSize, firstFrameByte);
    }

    /**
     * 根据时间和源文件生成MP3文件 （源文件mp3 比特率为cbr恒定比特率）
     *
     * @param header
     * @param targetFileStr
     * @param beginTime
     * @param endTime
     * @throws IOException
     */
    private void generateMp3ByTimeAndCBR(MP3AudioHeader header, String targetFileStr, long beginTime, long endTime) throws IOException {
        //获取音轨时长
        int trackLengthMs = header.getTrackLength() * 1000;
        long bitRateKbps = header.getBitRateAsNumber();
        //1KByte/s=8Kbps, bitRate *1024L / 8L / 1000L 转换为 bps 每毫秒
        //计算出开始字节位置
        long beginBitRateBpm = convertKbpsToBpm(bitRateKbps) * beginTime;
        //返回音乐数据的第一个字节
        long firstFrameByte = header.getMp3StartByte();
        //获取开始时间所在文件的字节位置
        long beginByte = firstFrameByte + beginBitRateBpm;
        //计算出结束字节位置
        long endByte = beginByte + convertKbpsToBpm(bitRateKbps) * (endTime - beginTime);
        if (endTime > trackLengthMs) {
            endByte = this.mSourceMp3File.length() - 1L;
        }
        generateTargetMp3File(targetFileStr, beginByte, endByte, firstFrameByte);
    }

    /**
     * 生成目标mp3文件
     *
     * @param targetFileStr
     * @param beginByte
     * @param endByte
     * @param firstFrameByte
     * @throws Exception
     */
    private void generateTargetMp3File(String targetFileStr,
                                       long beginByte, long endByte, long firstFrameByte) throws IOException {
        File file = new File(targetFileStr);
        //如果存在则删除
        FileUtils.checkAndDelFile(file);
        RandomAccessFile targetMp3File = null;
        RandomAccessFile sourceFile = null;
        try {
            targetMp3File = new RandomAccessFile(targetFileStr, "rw");
            sourceFile = new RandomAccessFile(mSourceMp3File, "rw");
            //write mp3 header info
            writeSourceToTargetFileWithBuffer(targetMp3File, sourceFile, firstFrameByte, 0);
            //write mp3 frame info
            int size = (int) (endByte - beginByte);
            writeSourceToTargetFileWithBuffer(targetMp3File, sourceFile, size, beginByte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sourceFile != null)
                sourceFile.close();
            if (targetMp3File != null)
                targetMp3File.close();
        }
    }

    /**
     * kbps 每秒千字节 转换到 bpm  每毫秒字节数
     *
     * @param bitRate
     * @return
     */
    private long convertKbpsToBpm(long bitRate) {
        return bitRate * 1024L / 8L / 1000L;
    }

    /**
     * 根据文件和大小以缓存的方式将源文件写入目标文件
     *
     * @param targetFile 写入的新文件
     * @param sourceFile 读取数据的文件
     * @param totalSize  需要读取并写入数据的总长度
     * @param offset     读取文件的偏移量
     * @throws IOException
     */
    private static void writeSourceToTargetFileWithBuffer(RandomAccessFile targetFile, RandomAccessFile sourceFile,
                                                          long totalSize, long offset) throws Exception {
        //缓存大小，每次写入指定数据防止内存泄漏
        int buffersize = BUFFER_SIZE;
        long count = totalSize / buffersize;
        if (count <= 1) {
            //文件总长度小于小于缓存大小情况
            writeSourceToTargetFile(targetFile, sourceFile, new byte[(int) totalSize], offset);
        } else {
            // 写入count后剩下的size
            long remainSize = totalSize % buffersize;
            byte data[] = new byte[buffersize];
            //读入文件时seek的偏移量
            for (int i = 0; i < count; i++) {
                writeSourceToTargetFile(targetFile, sourceFile, data, offset);
                offset += BUFFER_SIZE;
            }
            if (remainSize > 0) {
                writeSourceToTargetFile(targetFile, sourceFile, new byte[(int) remainSize], offset);
            }
        }
    }

    /**
     * 根据源文件和大小写入目标文件
     *
     * @param targetFile 输出的文件
     * @param sourceFile 读取的文件
     * @param data       输入输出的缓存数据
     * @param offset     读入文件时seek的偏移值
     */
    private static void writeSourceToTargetFile(RandomAccessFile targetFile, RandomAccessFile sourceFile,
                                                byte data[], long offset) throws Exception {
        sourceFile.seek(offset);
        sourceFile.read(data);
        long fileLength = targetFile.length();
        // 将写文件指针移到文件尾。
        targetFile.seek(fileLength);
        targetFile.write(data);
    }

    private static int getCBRFrameLength(String headerStr) {
        Pattern p = Pattern.compile("frame length:(\\d+)");
        Matcher m = p.matcher(headerStr);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
}