package com.xingcloud.sdk.serverside.sender;

import static com.genericlog.ErrorHttpRequestRecoder.logHttpError;
import static com.xingcloud.sdk.serverside.enums.ErrorHttpReason.ERROOOOOOR_URI;
import static com.xingcloud.sdk.serverside.enums.ErrorHttpReason.RESPONSE_WRONG;
import static com.xingcloud.sdk.serverside.enums.ErrorHttpReason.RESULTS_NOT_OK;
import static com.xingcloud.sdk.serverside.enums.ErrorHttpReason.UNPARSABLE_RES;
import static com.xingcloud.sdk.serverside.enums.ErrorHttpReason.UNREACHABLE_V4;

import com.xingcloud.sdk.serverside.LogSenderException;
import com.xingcloud.sdk.serverside.model.HttpRequestEntityGroup;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Z J Wu Date: 14-1-28 Time: 下午1:48 Package: com.xingcloud.sdk.serverside.sender
 */
public class SyncHttpLogSender extends HttpLogSender {
  private static final Logger LOGGER = Logger.getLogger(SyncHttpLogSender.class);
  private final HttpClient client = HttpClients.createDefault();

  public SyncHttpLogSender(LinkedBlockingQueue<HttpRequestEntityGroup> queue) {
    super(queue);
  }

  @Override
  public String send(URI uri) throws LogSenderException, IOException {
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = client.execute(httpGet);
    StatusLine statusLine = response.getStatusLine();
    int c = statusLine.getStatusCode();
    if (STATUS_CODE_OK == c) {
      return readResponse(response.getEntity());
    } else {
      throw new LogSenderException("Error return code: " + String.valueOf(c));
    }
  }

  private void valid(String resultString) throws IOException, LogSenderException {
    Map<String, Object> resultMap = RESULT_MAPPER.readValue(resultString, Map.class);
    if (!XA_V4_MSG_STATS_OK.equals(resultMap.get(XA_V4_MSG_STATS))) {
      throw new LogSenderException();
    }
  }

  @Override
  public void run() {
    LOGGER.info(Thread.currentThread().getName() + " inited.");
    HttpRequestEntityGroup entityGroup;
    URI uri;
    String result;
    try {
      while (true) {
        entityGroup = queue.take();
        if (entityGroup == null) {
          continue;
        }
        try {
          uri = entityGroup.toURI();
        } catch (LogSenderException e) {
          logHttpError(ERROOOOOOR_URI, e);
          continue;
        }
        try {
          result = send(uri);
        } catch (LogSenderException e) {
          logHttpError(RESPONSE_WRONG, e, uri);
          continue;
        } catch (IOException e) {
          logHttpError(UNREACHABLE_V4, e, uri);
          continue;
        }
        try {
          valid(result);
        } catch (IOException e) {
          logHttpError(UNPARSABLE_RES, e, uri);
        } catch (LogSenderException e) {
          logHttpError(RESULTS_NOT_OK, e, uri);
        }
//        LOGGER.info("Send log ok - " + uri.toString());
      }
    } catch (InterruptedException e) {
      LOGGER.warn("Thread - " + Thread.currentThread().getName() + " is interrupt.");
      Thread.currentThread().interrupt();
    }
  }

}
