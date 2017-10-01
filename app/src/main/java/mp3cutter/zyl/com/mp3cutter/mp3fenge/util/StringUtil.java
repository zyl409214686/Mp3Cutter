package mp3cutter.zyl.com.mp3cutter.mp3fenge.util;

import java.io.UnsupportedEncodingException;

public class StringUtil
{
  public static String convertEncode(String content, String oldEncode)
  {
    try
    {
      return new String(content.getBytes(oldEncode), "gbk");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return content;
  }

  public static boolean isBlank(String value)
  {
    boolean ret = false;
    if ((value != null) && (value.equals(""))) {
      ret = true;
    }
    return ret;
  }

  public static boolean isNull(String value)
  {
    return value == null;
  }

  public static boolean isNullOrBlank(String value)
  {
    return (isNull(value)) || (isBlank(value));
  }
}