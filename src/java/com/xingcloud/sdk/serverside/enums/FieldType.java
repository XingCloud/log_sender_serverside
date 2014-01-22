package com.xingcloud.sdk.serverside.enums;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:06 Package: com.xingcloud.sdk.serverside.enums
 */
public enum FieldType {
  UID("UserID"), EVENT("Event, v4 action."), UP("UserProperty, v4 update"), VAL("Field value"), TS("Timestamp"),
  POSITION("Row number.");

  private String description;

  FieldType(String description) {
    this.description = description;
  }
}
