package com.xingcloud.sdk.serverside.model;

import com.google.gson.annotations.Expose;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogLineDescriptorStatus;
import com.xingcloud.sdk.serverside.enums.LogType;

import java.util.Iterator;
import java.util.List;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午4:27 Package: com.xingcloud.sdk.serverside.model
 */
public class LogLineDescriptor {

  @Expose
  protected String name;
  @Expose
  protected LogType type;
  @Expose
  protected List<LogLineFieldDescriptor> items;

  protected LogLineFieldDescriptor positionFieldDescriptor;

  protected LogLineDescriptorStatus status;

  protected PositionRecorder positionRecorder;

  public LogLineDescriptor() {
  }

  public LogLineDescriptor(String name, LogType type, List<LogLineFieldDescriptor> items) {
    this.name = name;
    this.type = type;
    Iterator<LogLineFieldDescriptor> it = items.iterator();
    LogLineFieldDescriptor next;
    while (it.hasNext()) {
      next = it.next();
      if (next.getType().equals(FieldType.POSITION)) {
        positionFieldDescriptor = next;
        it.remove();
      }
    }
    this.items = items;
  }
}
