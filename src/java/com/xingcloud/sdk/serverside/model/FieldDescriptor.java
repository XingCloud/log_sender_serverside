package com.xingcloud.sdk.serverside.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.xingcloud.sdk.serverside.enums.FieldType;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午10:18 Package: com.xingcloud.sdk.serverside.model
 */
@JsonInclude(Include.NON_NULL)
public class FieldDescriptor {
  private String id;
  private String name;
  private FieldType type;
  private String of;

  public FieldDescriptor() {
  }

  public FieldDescriptor(String id, String name, FieldType type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  public FieldDescriptor(String id, String name, FieldType type, String of) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.of = of;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }

  public String getOf() {
    return of;
  }

  public void setOf(String of) {
    this.of = of;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FieldDescriptor)) {
      return false;
    }

    FieldDescriptor that = (FieldDescriptor) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
