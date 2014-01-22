package com.xingcloud.sdk.serverside.model;

import com.xingcloud.sdk.serverside.enums.FieldType;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午3:22 Package: com.xingcloud.sdk.serverside.model
 */
public class XALogEntity {
  private FieldType type;
  private String stringValue;

  public XALogEntity(FieldType type, String stringValue) {
    this.type = type;
    this.stringValue = stringValue;
  }
}
