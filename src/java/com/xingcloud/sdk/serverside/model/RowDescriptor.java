package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.LOG_TYPE_DB;
import static com.xingcloud.sdk.serverside.enums.FieldType.EVENT;
import static com.xingcloud.sdk.serverside.enums.FieldType.POSITION;
import static com.xingcloud.sdk.serverside.enums.FieldType.TS;
import static com.xingcloud.sdk.serverside.enums.FieldType.UID;
import static com.xingcloud.sdk.serverside.enums.FieldType.UP;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xingcloud.sdk.serverside.LogSenderException;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogLineDescriptorStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午4:27 Package: com.xingcloud.sdk.serverside.model
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = DBRowDescriptor.class, name = LOG_TYPE_DB)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RowDescriptor implements Runnable {
  protected String id;
  protected String projectId;
  protected String name;
  protected List<FieldDescriptor> items;

  @JsonIgnore
  protected CountDownLatch signal;
  @JsonIgnore
  protected LinkedBlockingQueue<HttpRequestEntityGroup> queue;
  protected TimeUnit sleepingTimeUnit;
  protected long sleepingTime;
  protected LogLineDescriptorStatus status;

  @JsonIgnore
  protected List<FieldDescriptor> eventItems;
  @JsonIgnore
  protected List<FieldDescriptor> upItems;
  @JsonIgnore
  protected List<FieldDescriptor> valueItems;
  @JsonIgnore
  protected FieldDescriptor uidFieldDescriptor;
  @JsonIgnore
  protected FieldDescriptor positionFieldDescriptor;
  @JsonIgnore
  protected FieldDescriptor globalTimestampFieldDescriptor;
  @JsonIgnore
  protected PositionRecorder positionRecorder;
  @JsonIgnore
  protected int eventSize = 0;
  @JsonIgnore
  protected int upSize = 0;

  public RowDescriptor() {
    this.status = LogLineDescriptorStatus.INITED;
  }

  public void initItems(LinkedBlockingQueue<HttpRequestEntityGroup> queue) throws LogSenderException {
    this.id = this.projectId + "." + this.name;
    this.queue = queue;
    Iterator<FieldDescriptor> it = items.iterator();
    FieldDescriptor next;
    FieldType fieldType;
    while (it.hasNext()) {
      next = it.next();
      fieldType = next.getType();
      if (POSITION.equals(fieldType)) {
        positionFieldDescriptor = next;
      } else if (UID.equals(fieldType)) {
        uidFieldDescriptor = next;
      } else if (EVENT.equals(fieldType)) {
        ++eventSize;
        if (this.eventItems == null) {
          this.eventItems = new ArrayList<>(1);
        }
        this.eventItems.add(next);
      } else if (UP.equals(fieldType)) {
        ++upSize;
        if (this.upItems == null) {
          this.upItems = new ArrayList<>(1);
        }
        this.upItems.add(next);
      } else if (TS.equals(fieldType)) {
        this.globalTimestampFieldDescriptor = next;
      } else {
        if (this.valueItems == null) {
          this.valueItems = new ArrayList<>(1);
        }
        this.valueItems.add(next);
      }
    }
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<FieldDescriptor> getItems() {
    return items;
  }

  public void setItems(List<FieldDescriptor> items) {
    this.items = items;
  }

  public List<FieldDescriptor> getEventItems() {
    return eventItems;
  }

  public void setEventItems(List<FieldDescriptor> eventItems) {
    this.eventItems = eventItems;
  }

  public List<FieldDescriptor> getUpItems() {
    return upItems;
  }

  public void setUpItems(List<FieldDescriptor> upItems) {
    this.upItems = upItems;
  }

  public List<FieldDescriptor> getValueItems() {
    return valueItems;
  }

  public void setValueItems(List<FieldDescriptor> valueItems) {
    this.valueItems = valueItems;
  }

  public FieldDescriptor getUidFieldDescriptor() {
    return uidFieldDescriptor;
  }

  public void setUidFieldDescriptor(FieldDescriptor uidFieldDescriptor) {
    this.uidFieldDescriptor = uidFieldDescriptor;
  }

  public FieldDescriptor getPositionFieldDescriptor() {
    return positionFieldDescriptor;
  }

  public void setPositionFieldDescriptor(FieldDescriptor positionFieldDescriptor) {
    this.positionFieldDescriptor = positionFieldDescriptor;
  }

  public FieldDescriptor getGlobalTimestampFieldDescriptor() {
    return globalTimestampFieldDescriptor;
  }

  public void setGlobalTimestampFieldDescriptor(FieldDescriptor globalTimestampFieldDescriptor) {
    this.globalTimestampFieldDescriptor = globalTimestampFieldDescriptor;
  }

  public LogLineDescriptorStatus getStatus() {
    return status;
  }

  public void setStatus(LogLineDescriptorStatus status) {
    this.status = status;
  }

  public PositionRecorder getPositionRecorder() {
    return positionRecorder;
  }

  public void setPositionRecorder(PositionRecorder positionRecorder) {
    this.positionRecorder = positionRecorder;
  }

  public CountDownLatch getSignal() {
    return signal;
  }

  public void setSignal(CountDownLatch signal) {
    this.signal = signal;
  }

  public int getEventSize() {
    return eventSize;
  }

  public void setEventSize(int eventSize) {
    this.eventSize = eventSize;
  }

  public int getUpSize() {
    return upSize;
  }

  public void setUpSize(int upSize) {
    this.upSize = upSize;
  }

  public TimeUnit getSleepingTimeUnit() {
    return sleepingTimeUnit;
  }

  public void setSleepingTimeUnit(TimeUnit sleepingTimeUnit) {
    this.sleepingTimeUnit = sleepingTimeUnit;
  }

  public long getSleepingTime() {
    return sleepingTime;
  }

  public void setSleepingTime(long sleepingTime) {
    this.sleepingTime = sleepingTime;
  }
}
