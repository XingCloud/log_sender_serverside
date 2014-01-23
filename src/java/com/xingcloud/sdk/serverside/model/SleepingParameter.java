package com.xingcloud.sdk.serverside.model;

import java.util.concurrent.TimeUnit;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午3:47 Package: com.xingcloud.sdk.serverside.model
 */
public class SleepingParameter {
  private TimeUnit timeUnit;
  private long time;

  public SleepingParameter(TimeUnit timeUnit, long time) {
    this.timeUnit = timeUnit;
    this.time = time;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public long getTime() {
    return time;
  }
}
