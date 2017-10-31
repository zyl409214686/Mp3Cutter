package com.zyl.mp3cutter.mp3separate.bean;

import com.zyl.mp3cutter.mp3separate.util.FileUtil;
import com.zyl.mp3cutter.mp3separate.util.StringUtil;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Mp3Fenge
{
  private static double TIME_PER_FRAME = 26.122448979591837D;
  private File mp3File;
  private Mp3Info mp3Info;

  public Mp3Fenge(File mp3File)
  {
    this.mp3File = mp3File;
  }

  public File getMp3File()
  {
    return this.mp3File;
  }

  public void setMp3File(File mp3File)
  {
    this.mp3File = mp3File;
  }

  public Mp3Info getMp3Info()
  {
    if (this.mp3Info == null) {
      try {
        MP3File mp3 = new MP3File(this.mp3File);
        ID3v1Tag v1 = mp3.getID3v1Tag();
        String encoding = v1.getEncoding();
        MP3AudioHeader header = (MP3AudioHeader)mp3.getAudioHeader();
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

  private byte[] getDataByBitRate(int beginTime, int endTime)
  {
    byte[] result = (byte[])null;
    RandomAccessFile rMp3File = null;
    try {
      MP3File mp3 = new MP3File(this.mp3File);
      MP3AudioHeader header = (MP3AudioHeader)mp3.getAudioHeader();
      if (header.isVariableBitRate()) {
        
        result = (byte[])null;
      } else {
        long mp3StartIndex = header.getMp3StartByte();
        int trackLengthMs = header.getTrackLength() * 1000;
        long bitRate = header.getBitRateAsNumber();
        long beginIndex = bitRate * 1024L / 8L / 1000L * beginTime + mp3StartIndex;
        long endIndex = beginIndex + bitRate * 1024L / 8L / 1000L * (endTime - beginTime);
        if (endTime > trackLengthMs) {
          endIndex = this.mp3File.length() - 1L;
        }
        rMp3File = new RandomAccessFile(this.mp3File, "r");
        rMp3File.seek(beginIndex);
        int size = (int)(endIndex - beginIndex);
        result = new byte[size];
        rMp3File.read(result);
      }
    } catch (Exception e) {
      e.printStackTrace();

      if (rMp3File != null)
        try {
          rMp3File.close();
        } catch (IOException e2) {
          e.printStackTrace();
        }
    }
    finally
    {
      if (rMp3File != null) {
        try {
          rMp3File.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

  private static int getCBRFrameLength(String headerStr)
  {
    Pattern p = Pattern.compile("frame length:(\\d+)");
    Matcher m = p.matcher(headerStr);
    if (m.find()) {
      return Integer.parseInt(m.group(1));
    }
    return 0;
  }

  public byte[] getDataByTime(int beginTime, int endTime)
  {
    return getDataByBitRate(beginTime, endTime);
  }

  public boolean generateNewMp3ByTime(File newMp3, int beginTime, int endTime)
  {
    byte[] frames = getDataByTime(beginTime, endTime);
    if ((frames == null) || (frames.length < 1)) {
      return false;
    }
    List mp3datas = new ArrayList();
    mp3datas.add(frames);
    return FileUtil.generateFile(newMp3, mp3datas);
  }

  public static void main(String[] args) {
    Mp3Fenge helper = new Mp3Fenge(new File("testdata/eyes_on_me.mp3"));

    helper.generateNewMp3ByTime(new File("testdata/e1.mp3"), 307000, 315000);

    byte[] e2 = helper.getDataByTime(70000, 76000);
    List mp3datas = new ArrayList();
    mp3datas.add(e2);
    FileUtil.generateFile(new File("testdata/e2.mp3"), mp3datas);
  }
}