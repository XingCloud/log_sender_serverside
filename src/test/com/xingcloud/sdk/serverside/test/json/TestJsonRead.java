package com.xingcloud.sdk.serverside.test.json;

import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.json.JsonReaderWriter;
import com.xingcloud.sdk.serverside.model.RowDescriptor;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * User: Z J Wu Date: 14-1-24 Time: 上午11:58 Package: com.xingcloud.sdk.serverside.test.json
 */
public class TestJsonRead {

  public static void main(String[] args) throws IOException, LogLineSenderException, InterruptedException {
    CountDownLatch signal = new CountDownLatch(1);
    String filePath = "./example/event_and_user_property/game_point.json";
    RowDescriptor rd = JsonReaderWriter.parseAndInit(filePath, signal);
    System.out.println(JsonReaderWriter.toJson(rd));
    new Thread(rd, "Thread-LogLineFetcher(" + rd.getName() + ")").start();
    signal.await();
  }
}
