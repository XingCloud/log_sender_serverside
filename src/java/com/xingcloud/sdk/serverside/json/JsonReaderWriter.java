package com.xingcloud.sdk.serverside.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.model.RowDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * User: Z J Wu Date: 14-1-24 Time: 上午11:07 Package: com.xingcloud.sdk.serverside
 */
public class JsonReaderWriter {

  public static RowDescriptor parseAndInit(String filePath, CountDownLatch signal) throws LogLineSenderException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
    RowDescriptor rd;
    try {
      rd = mapper.readValue(new File(filePath), RowDescriptor.class);
    } catch (IOException e) {
      throw new LogLineSenderException(e);
    }
    rd.initItems(signal);
    return rd;
  }

  public static String toJson(RowDescriptor rd) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(new CamelCaseNamingStrategy());
    return mapper.writeValueAsString(rd);
  }

}
