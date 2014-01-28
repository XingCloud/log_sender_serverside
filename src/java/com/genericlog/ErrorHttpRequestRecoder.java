package com.genericlog;

import static com.xingcloud.sdk.serverside.LogSenderServerSideUtils.decodeURI;

import com.xingcloud.sdk.serverside.enums.ErrorHttpReason;
import org.apache.log4j.Logger;

import java.net.URI;

/**
 * User: Z J Wu Date: 14-1-28 Time: 下午2:42 Package: com.xingcloud.sdk.serverside
 */
public class ErrorHttpRequestRecoder {

  private static final Logger LOGGER = Logger.getLogger(ErrorHttpRequestRecoder.class);

  public static void logHttpError(ErrorHttpReason reason, Exception e, URI uri) {
    LOGGER.error(reason.name() + " " + reason.getMsg() + " " + decodeURI(uri), e);
  }

  public static void logHttpError(ErrorHttpReason reason, Exception e) {
    LOGGER.error(reason.name() + " " + reason.getMsg(), e);
  }
}
