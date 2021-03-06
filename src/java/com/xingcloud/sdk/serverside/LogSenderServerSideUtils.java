package com.xingcloud.sdk.serverside;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午4:54 Package: com.xingcloud.sdk.serverside
 */
public class LogSenderServerSideUtils {
  public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;
  public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;

  public static String decodeURI(URI uri) {
    try {
      return uri == null ? null : URLDecoder.decode(uri.toString(), "utf8");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  public static void closeCloseable(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static <T extends AutoCloseable> void closeAutoCloseable(T closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static byte[] toBytes(long val) {
    byte[] b = new byte[8];
    for (int i = 7; i > 0; i--) {
      b[i] = (byte) val;
      val >>>= 8;
    }
    b[0] = (byte) val;
    return b;
  }

  public static byte[] toBytes(int val) {
    byte[] b = new byte[4];
    for (int i = 3; i > 0; i--) {
      b[i] = (byte) val;
      val >>>= 8;
    }
    b[0] = (byte) val;
    return b;
  }

  public static int toInt(byte[] bytes) {
    int n = 0;
    for (int i = 0; i < SIZEOF_INT; i++) {
      n <<= 8;
      n ^= bytes[i] & 0xFF;
    }
    return n;
  }

  public static long toLong(byte[] bytes) {
    long l = 0;
    for (int i = 0; i < SIZEOF_LONG; i++) {
      l <<= 8;
      l ^= bytes[i] & 0xFF;
    }
    return l;
  }

  public static void main(String[] args) {
    int a = 569389205;
    System.out.println(a == toInt(toBytes(a)));

    long c = 564564564564564l;
    System.out.println(c == toLong(toBytes(c)));
  }

}
