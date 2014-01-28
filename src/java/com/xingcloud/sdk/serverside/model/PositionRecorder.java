package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideUtils.toBytes;
import static com.xingcloud.sdk.serverside.LogSenderServerSideUtils.toInt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * User: Z J Wu Date: 14-1-22 Time: 上午10:47 Package: com.xingcloud.sdk.serverside.model
 */
public class PositionRecorder {

  private File positionFile;

  public PositionRecorder(String positionFilePath, String positionFileName) {
    File path = new File(positionFilePath);
    if (!path.exists()) {
      path.mkdir();
    }
    this.positionFile = new File(positionFilePath + File.separatorChar + positionFileName);
  }

  public synchronized int read() throws IOException {
    if (!positionFile.exists()) {
      return 0;
    }
    byte[] bytes = new byte[4];
    try (FileInputStream fis = new FileInputStream(positionFile);) {
      fis.read(bytes);
    }
    return toInt(bytes);
  }

  public synchronized void write(int lineNumber) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(positionFile);) {
      fos.write(toBytes(lineNumber));
    }
  }

  public static void main(String[] args) throws IOException {
    String path = "d:/positions";
    String file = "coin.line";
    PositionRecorder pr = new PositionRecorder(path, file);
    pr.write(new Random().nextInt());
    System.out.println(pr.read());
  }
}
