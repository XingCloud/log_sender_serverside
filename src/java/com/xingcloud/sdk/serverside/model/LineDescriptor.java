package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.enums.FieldType.EVENT;
import static com.xingcloud.sdk.serverside.enums.FieldType.POSITION;
import static com.xingcloud.sdk.serverside.enums.FieldType.UID;
import static com.xingcloud.sdk.serverside.enums.FieldType.UP;

import com.google.gson.annotations.Expose;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogLineDescriptorStatus;
import com.xingcloud.sdk.serverside.enums.LogType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午4:27 Package: com.xingcloud.sdk.serverside.model
 */
public class LineDescriptor {
  @Expose
  protected String projectId;
  @Expose
  protected String name;
  @Expose
  protected LogType type;

  protected List<FieldDescriptor> eventItems;
  protected List<FieldDescriptor> upItems;
  protected List<FieldDescriptor> valueOrTimestampItems;

  protected FieldDescriptor uidFieldDescriptor;
  protected FieldDescriptor positionFieldDescriptor;

  protected LogLineDescriptorStatus status;

  protected PositionRecorder positionRecorder;

  protected CountDownLatch signal;

  protected volatile boolean enabled;

  protected SleepingParameter sleepingParameter;

  protected int eventSize = 0;
  protected int upSize = 0;

  public LineDescriptor() {
  }

  public LineDescriptor(boolean enabled, CountDownLatch signal, SleepingParameter sleepingParameter, String projectId,
                        String name, LogType type, List<FieldDescriptor> items) {
    this.projectId = projectId;
    this.enabled = enabled;
    this.signal = signal;
    this.sleepingParameter = sleepingParameter;
    this.name = name;
    this.type = type;
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
      } else {
        if (this.valueOrTimestampItems == null) {
          this.valueOrTimestampItems = new ArrayList<>(1);
        }
        this.valueOrTimestampItems.add(next);
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

  public LogType getType() {
    return type;
  }

  public void setType(LogType type) {
    this.type = type;
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

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public SleepingParameter getSleepingParameter() {
    return sleepingParameter;
  }

  public void setSleepingParameter(SleepingParameter sleepingParameter) {
    this.sleepingParameter = sleepingParameter;
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

  public List<FieldDescriptor> getValueOrTimestampItems() {
    return valueOrTimestampItems;
  }

  public void setValueOrTimestampItems(List<FieldDescriptor> valueOrTimestampItems) {
    this.valueOrTimestampItems = valueOrTimestampItems;
  }
}
