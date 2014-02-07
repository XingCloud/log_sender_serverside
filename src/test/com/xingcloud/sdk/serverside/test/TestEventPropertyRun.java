package com.xingcloud.sdk.serverside.test;

import com.xingcloud.sdk.serverside.LogSenderException;
import com.xingcloud.sdk.serverside.json.JsonReaderWriter;
import com.xingcloud.sdk.serverside.model.HttpRequestEntityGroup;
import com.xingcloud.sdk.serverside.model.RowDescriptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:19 Package: com.xingcloud.sdk.serverside.test
 */
public class TestEventPropertyRun {
  public static void main(String[] args) throws LogSenderException, InterruptedException {
    CountDownLatch signal = new CountDownLatch(1);
    RowDescriptor rd = JsonReaderWriter.parseAndInit("./example/event_and_user_property/game_point.json",
                                                     new LinkedBlockingQueue<HttpRequestEntityGroup>());
    new Thread(rd, "Thread-LogFetcher(" + rd.getProjectId() + "." + rd.getName() + ")").start();
    signal.await();
  }
}
