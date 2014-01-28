package com.xingcloud.sdk.serverside;

import com.xingcloud.sdk.serverside.json.JsonReaderWriter;
import com.xingcloud.sdk.serverside.model.HttpRequestEntityGroup;
import com.xingcloud.sdk.serverside.model.RowDescriptor;
import com.xingcloud.sdk.serverside.sender.SyncHttpLogSender;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Z J Wu Date: 14-1-26 Time: 上午10:12 Package: com.xingcloud.sdk.serverside
 */
public class MainRun {

  private static final Logger LOGGER = Logger.getLogger(MainRun.class);

  public static void main(String[] args) throws InterruptedException {
    int files = ArrayUtils.isEmpty(args) ? 0 : args.length;
    if (files == 0) {
      LOGGER.error("There is no any description file.");
      System.exit(1);
    }

    int senders = 10;
    LinkedBlockingQueue<HttpRequestEntityGroup> queue = new LinkedBlockingQueue<>();
    for (int i = 0; i < senders; i++) {
      new Thread(new SyncHttpLogSender(queue), "LogSenderThread-" + (i + 1)).start();
    }

    CountDownLatch signal = new CountDownLatch(files);
    List<RowDescriptor> rds = new ArrayList<>(files);
    for (int i = 0; i < files; i++) {
      try {
        rds.add(JsonReaderWriter.parseAndInit(args[i], signal, queue));
      } catch (LogSenderException e) {
        LOGGER.error("Cannot parse description file(" + args[i] + ")", e);
      }
    }
    if (CollectionUtils.isEmpty(rds)) {
      LOGGER.error("No valid descriptor created.");
      System.exit(1);
    }

    for (RowDescriptor rd : rds) {
      new Thread(rd, "Thread-LogFetcher(" + rd.getProjectId() + "." + rd.getName() + ")").start();
    }
    signal.await();
    LOGGER.info("Log sender shut down.");
  }

}
