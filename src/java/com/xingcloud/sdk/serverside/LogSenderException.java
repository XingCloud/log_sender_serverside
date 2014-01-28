package com.xingcloud.sdk.serverside;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午2:24 Package: com.xingcloud.sdk.serverside
 */
public class LogSenderException extends Exception {
  public LogSenderException() {
    super();
  }

  public LogSenderException(String message) {
    super(message);
  }

  public LogSenderException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogSenderException(Throwable cause) {
    super(cause);
  }
}
