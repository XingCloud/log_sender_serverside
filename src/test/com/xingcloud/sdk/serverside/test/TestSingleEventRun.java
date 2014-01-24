package com.xingcloud.sdk.serverside.test;

import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.json.JsonReaderWriter;
import com.xingcloud.sdk.serverside.model.RowDescriptor;

import java.util.concurrent.CountDownLatch;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:19 Package: com.xingcloud.sdk.serverside.test
 */
public class TestSingleEventRun {
  public static void main(String[] args) throws LogLineSenderException, InterruptedException {
    CountDownLatch signal = new CountDownLatch(1);
    RowDescriptor rd = JsonReaderWriter.parseAndInit("./example/single_event/coin.json", signal);
    new Thread(rd, "Thread-LogFetcher(" + rd.getProjectId() + "." + rd.getName() + ")").start();
    signal.await();
  }
}
