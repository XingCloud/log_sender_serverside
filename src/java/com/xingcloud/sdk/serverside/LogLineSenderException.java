package com.xingcloud.sdk.serverside;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午2:24 Package: com.xingcloud.sdk.serverside
 */
public class LogLineSenderException extends Exception {
  public LogLineSenderException() {
    super();
  }

  public LogLineSenderException(String message) {
    super(message);
  }

  public LogLineSenderException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogLineSenderException(Throwable cause) {
    super(cause);
  }
}
