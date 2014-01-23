package com.xingcloud.sdk.serverside.model;

import com.xingcloud.sdk.serverside.enums.FieldType;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午3:22 Package: com.xingcloud.sdk.serverside.model
 */
public class HttpRequestEntity {
  private String id;
  private FieldType type;
  private String fieldName;
  private long fieldValue;
  private long timestamp;

  public HttpRequestEntity(String id, FieldType type, String fieldName) {
    this.id = id;
    this.type = type;
    this.fieldName = fieldName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public long getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(long fieldValue) {
    this.fieldValue = fieldValue;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
