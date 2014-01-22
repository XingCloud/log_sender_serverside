package com.xingcloud.sdk.serverside;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午11:13 Package: com.xingcloud.sdk.serverside
 */
public class LogSenderServerSideConstants {

  public static final Gson DEFAULT_GSON_PLAIN = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
}
