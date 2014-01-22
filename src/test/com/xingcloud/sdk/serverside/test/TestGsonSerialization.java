package com.xingcloud.sdk.serverside.test;

import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.LogSenderServerSideConstants;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogType;
import com.xingcloud.sdk.serverside.model.DBLogLineDescriptor;
import com.xingcloud.sdk.serverside.model.LogLineFieldDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:19 Package: com.xingcloud.sdk.serverside.test
 */
public class TestGsonSerialization {
  public static void main(String[] args) throws LogLineSenderException {
    List<LogLineFieldDescriptor> items = new ArrayList<LogLineFieldDescriptor>(4);
    items.add(
      new LogLineFieldDescriptor("CoinAction01.event-val", "column_c", FieldType.VAL, "CoinAction01.event-action"));
    items.add(new LogLineFieldDescriptor("CoinAction01.position", "column_d", FieldType.POSITION));
    items.add(new LogLineFieldDescriptor("CoinAction01.event-action", "column_b", FieldType.EVENT));
    items.add(new LogLineFieldDescriptor("CoinAction01.uid", "column_a", FieldType.UID));

    DBLogLineDescriptor logLineDescriptor = new DBLogLineDescriptor("coin", LogType.DB, items, "localhost", 3306,
                                                                    "root", "1");
//    System.out.println(LogSenderServerSideConstants.DEFAULT_GSON_PLAIN.toJson(logLineDescriptor));
    System.out.println(logLineDescriptor.getSqlQuery());
  }
}
