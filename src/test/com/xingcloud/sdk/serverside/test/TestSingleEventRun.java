package com.xingcloud.sdk.serverside.test;

import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogType;
import com.xingcloud.sdk.serverside.model.DBLineDescriptor;
import com.xingcloud.sdk.serverside.model.FieldDescriptor;
import com.xingcloud.sdk.serverside.model.SleepingParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:19 Package: com.xingcloud.sdk.serverside.test
 */
public class TestSingleEventRun {
  public static void main(String[] args) throws LogLineSenderException, InterruptedException {
    CountDownLatch signal = new CountDownLatch(1);
    boolean enabled = true;
    SleepingParameter sp = new SleepingParameter(TimeUnit.SECONDS, 5);
    List<FieldDescriptor> items = new ArrayList<FieldDescriptor>(4);
    items.add(new FieldDescriptor("CoinAction01.event-val", "number", FieldType.VAL, "CoinAction01.event-action"));
    items.add(new FieldDescriptor("CoinAction01.event-ts", "timestamp", FieldType.TS, "CoinAction01.event-action"));
    items.add(new FieldDescriptor("CoinAction01.position", "id", FieldType.POSITION));
    items.add(new FieldDescriptor("CoinAction01.event-action", "event", FieldType.EVENT));
    items.add(new FieldDescriptor("CoinAction01.uid", "uid", FieldType.UID));

    DBLineDescriptor logLineDescriptor = new DBLineDescriptor(enabled, signal, sp, "fhw", "game_log.coin",
                                                                    LogType.DB, items, "localhost", 3306, "root", "1");
    new Thread(logLineDescriptor, "Thread-LogLineFetcher(" + logLineDescriptor.getName() + ")").start();
    signal.await();

  }
}