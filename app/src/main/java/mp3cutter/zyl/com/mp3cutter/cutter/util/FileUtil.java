package mp3cutter.zyl.com.mp3cutter.cutter.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class FileUtil
{
  public static boolean generateFile(File file, List<byte[]> datas)
  {
    BufferedOutputStream bos = null;
    try {
      bos = new BufferedOutputStream(new FileOutputStream(file));
      for (byte[] data : datas) {
        bos.write(data);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  public static boolean appendData(File file, byte[][] datas)
  {
    RandomAccessFile rfile = null;
    try {
      rfile = new RandomAccessFile(file, "rw");
      rfile.seek(file.length());
      for (byte[] data : datas) {
        rfile.write(data);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (rfile != null) {
        try {
          rfile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }
}