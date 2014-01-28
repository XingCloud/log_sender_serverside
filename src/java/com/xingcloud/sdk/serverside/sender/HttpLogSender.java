package com.xingcloud.sdk.serverside.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingcloud.sdk.serverside.LogSenderException;
import com.xingcloud.sdk.serverside.model.HttpRequestEntityGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Z J Wu Date: 14-1-28 Time: 下午1:49 Package: com.xingcloud.sdk.serverside.sender
 */
public abstract class HttpLogSender implements Runnable {
  protected LinkedBlockingQueue<HttpRequestEntityGroup> queue;
  protected final ObjectMapper RESULT_MAPPER = new ObjectMapper();
  protected static final int STATUS_CODE_OK = 200;
  protected static final String XA_V4_MSG_STATS = "stats";
  protected static final String XA_V4_MSG_STATS_OK = "ok";

  protected HttpLogSender(LinkedBlockingQueue<HttpRequestEntityGroup> queue) {
    this.queue = queue;
  }

  public abstract String send(URI uri) throws LogSenderException, IOException;

  protected String readResponse(HttpEntity responseEntity) throws IOException {
    if (responseEntity == null) {
      return null;
    }
    StringBuilder responseString = new StringBuilder();
    String line;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));) {
      while ((line = reader.readLine()) != null) {
        line = StringUtils.trimToNull(line);
        if (StringUtils.isNotEmpty(line)) {
          responseString.append(line);
        }
      }
    }
    return responseString.toString();
  }
}
