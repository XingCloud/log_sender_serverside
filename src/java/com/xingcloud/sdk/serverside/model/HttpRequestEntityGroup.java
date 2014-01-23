package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.EVENT_KEYWORDS;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.HOST;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.HTTP;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PARAMETER_VAL_SEPARATOR;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PATH_PREFIX;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PORT;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.UP_KEYWORDS;
import static com.xingcloud.sdk.serverside.enums.FieldType.EVENT;

import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.enums.FieldType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Z J Wu Date: 14-1-22 Time: 下午6:16 Package: com.xingcloud.sdk.serverside.model
 */
public class HttpRequestEntityGroup {

  private String projectId;
  private String uid;
  private Map<String, HttpRequestEntity> entityMap;

  public HttpRequestEntityGroup(String projectId, String uid, int mapSize) {
    this.projectId = projectId;
    this.uid = uid;
    this.entityMap = new HashMap<>(mapSize);
  }

  public URI toURI() throws LogLineSenderException, URISyntaxException {
    URIBuilder builder = new URIBuilder();
    builder.setScheme(HTTP);
    builder.setHost(HOST);
    builder.setPort(PORT);
    if (StringUtils.isBlank(uid)) {
      throw new LogLineSenderException("Empty uid field.");
    }
    String path = PATH_PREFIX + "/" + projectId + "/" + uid;
    builder.setPath(path);

    int eventCNT = 0, upCNT = 0;
    FieldType ft;
    String paramName;
    long val, ts;
    StringBuilder valueSB;
    for (HttpRequestEntity entity : entityMap.values()) {
      ft = entity.getType();
      if (EVENT.equals(ft)) {
        paramName = EVENT_KEYWORDS + eventCNT;
        ++eventCNT;
      } else {
        paramName = UP_KEYWORDS + upCNT;
        ++upCNT;
      }
      valueSB = new StringBuilder(entity.getFieldName());
      val = entity.getFieldValue();
      ts = entity.getTimestamp();
      if (val > 0) {
        valueSB.append(PARAMETER_VAL_SEPARATOR);
        valueSB.append(val);
      }
      if (ts > 0) {
        if (val == 0) {
          valueSB.append(PARAMETER_VAL_SEPARATOR);
        }
        valueSB.append(PARAMETER_VAL_SEPARATOR);
        valueSB.append(ts);
      }
      builder.addParameter(paramName, valueSB.toString());
    }

    URI uri = builder.build();
    return uri;
  }

  public Map<String, HttpRequestEntity> getEntityMap() {
    return entityMap;
  }
}
