package com.xingcloud.sdk.serverside;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:13 Package: com.xingcloud.sdk.serverside
 */
public class LogSenderServerSideConstants {
  public static final String HTTP = "http";
  public static final String HOST = "xa.xingcloud.com";
  public static final int PORT = 80;
  public static final String PATH_PREFIX = "/v4";
  public static final String EVENT_KEYWORDS = "action";
  public static final String UP_KEYWORDS = "update";
  public static final String PARAMETER_VAL_SEPARATOR = ",";

  public static final Gson DEFAULT_GSON_PLAIN = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
}
