package com.xingcloud.sdk.serverside.enums;

/**
 * User: Z J Wu Date: 14-1-28 Time: 下午2:43 Package: com.xingcloud.sdk.serverside.enums
 */
public enum ErrorHttpReason {
  ERROOOOOOR_URI("Error uri"),
  RESPONSE_WRONG("Xingcloud V4 returns wrong code."),
  UNREACHABLE_V4("Xingcloud V4 is unreachable now."),
  UNPARSABLE_RES("Cannot parse result from V4."),
  RESULTS_NOT_OK("V4 said this request is invalid.");

  private String msg;

  ErrorHttpReason(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
