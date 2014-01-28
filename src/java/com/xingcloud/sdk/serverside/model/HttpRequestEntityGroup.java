package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.EVENT_KEYWORDS;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.GLOBAL_TS;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.HOST;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.HTTP;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PARAMETER_VAL_SEPARATOR;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PATH_PREFIX;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.PORT;
import static com.xingcloud.sdk.serverside.LogSenderServerSideConstants.UP_KEYWORDS;
import static com.xingcloud.sdk.serverside.enums.FieldType.EVENT;

import com.xingcloud.sdk.serverside.LogSenderException;
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
  private long globalTimestamp;
  private Map<String, HttpRequestEntity> entityMap;

  public HttpRequestEntityGroup(String projectId, String uid, int mapSize) {
    this.projectId = projectId;
    this.uid = uid;
    this.entityMap = new HashMap<>(mapSize);
  }

  public HttpRequestEntityGroup(String projectId, String uid, int mapSize, long globalTimestamp) {
    this.globalTimestamp = globalTimestamp;
    this.projectId = projectId;
    this.uid = uid;
    this.entityMap = new HashMap<>(mapSize);
  }

  public URI toURI() throws LogSenderException {
    URIBuilder builder = new URIBuilder();
    builder.setScheme(HTTP);
    builder.setHost(HOST);
    builder.setPort(PORT);
    if (StringUtils.isBlank(uid)) {
      throw new LogSenderException("Empty uid field.");
    }
    String path = PATH_PREFIX + "/" + projectId + "/" + uid;
    builder.setPath(path);
    if (globalTimestamp > 0) {
      builder.addParameter(GLOBAL_TS, String.valueOf(globalTimestamp));
    }

    int eventCNT = 0, upCNT = 0;
    FieldType ft;
    String paramName, val;
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
      if (StringUtils.isNoneBlank(val)) {
        valueSB.append(PARAMETER_VAL_SEPARATOR);
        valueSB.append(val);
      }
      builder.addParameter(paramName, valueSB.toString());
    }

    URI uri;
    try {
      uri = builder.build();
    } catch (URISyntaxException e) {
      throw new LogSenderException(e);
    }
    return uri;
  }

  public Map<String, HttpRequestEntity> getEntityMap() {
    return entityMap;
  }
}
