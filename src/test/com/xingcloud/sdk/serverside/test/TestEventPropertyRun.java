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
public class TestEventPropertyRun {
  public static void main(String[] args) throws LogLineSenderException, InterruptedException {
    CountDownLatch signal = new CountDownLatch(1);
    boolean enabled = true;
    SleepingParameter sp = new SleepingParameter(TimeUnit.SECONDS, 5);
    List<FieldDescriptor> items = new ArrayList<FieldDescriptor>(4);
    items.add(new FieldDescriptor("GamePoint.EventVal", "event_val", FieldType.VAL, "GamePoint.Event"));
    items.add(new FieldDescriptor("GamePoint.Event", "event", FieldType.EVENT));
    items
      .add(new FieldDescriptor("GamePoint.UserPropertyVal", "property_val", FieldType.VAL, "GamePoint.UserProperty"));
    items.add(new FieldDescriptor("GamePoint.UserProperty", "property", FieldType.UP));
    items.add(new FieldDescriptor("GamePoint.position", "id", FieldType.POSITION));
    items.add(new FieldDescriptor("GamePoint.uid", "uid", FieldType.UID));
    items.add(new FieldDescriptor("GamePoint.ts", "timestamp", FieldType.TS, "GamePoint.Event"));

    DBLineDescriptor logLineDescriptor = new DBLineDescriptor(enabled, signal, sp, "fhw", "game_log.game_point",
                                                              LogType.DB, items, "localhost", 3306, "root", "1");
    new Thread(logLineDescriptor, "Thread-LogLineFetcher(" + logLineDescriptor.getName() + ")").start();
    signal.await();

  }
}
