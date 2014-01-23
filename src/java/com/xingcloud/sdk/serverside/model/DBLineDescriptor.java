package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideUtils.closeAutoCloseable;
import static com.xingcloud.sdk.serverside.enums.FieldType.EVENT;
import static com.xingcloud.sdk.serverside.enums.FieldType.TS;
import static com.xingcloud.sdk.serverside.enums.FieldType.UP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.enums.FieldType;
import com.xingcloud.sdk.serverside.enums.LogLineDescriptorStatus;
import com.xingcloud.sdk.serverside.enums.LogType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午10:14 Package: com.xingcloud.sdk.serverside.model
 */
public class DBLineDescriptor extends LineDescriptor implements Closeable, AutoCloseable, Runnable {
  private final String SQL_FIELD_SEPARATOR = ", ";
  private static final Logger LOGGER = Logger.getLogger(DBLineDescriptor.class);
  @Expose
  @SerializedName("db_host")
  private String host;
  @Expose
  @SerializedName("db_port")
  private int port;
  @Expose
  @SerializedName("db_user_name")
  private String dbUserName;
  @Expose
  @SerializedName("db_password")
  private String dbPassword;

  private BasicDataSource dataSource;

  private List<String> rowNames;

  private String sqlQuery;

  public DBLineDescriptor() {
  }

  public DBLineDescriptor(boolean enabled, CountDownLatch signal, SleepingParameter sleepingParameter, String projectId,
                          String name, LogType type, List<FieldDescriptor> items, String host, int port,
                          String dbUserName, String dbPassword) throws LogLineSenderException {
    super(enabled, signal, sleepingParameter, projectId, name, type, items);
    this.host = host;
    this.port = port;
    this.dbUserName = dbUserName;
    this.dbPassword = dbPassword;
    setupDataSource();
    testConnection();
    rowNames = new ArrayList<>(items.size() - 1);
    buildQuerySql();
    initPositionFile();
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getDbUserName() {
    return dbUserName;
  }

  public void setDbUserName(String dbUserName) {
    this.dbUserName = dbUserName;
  }

  public String getSqlQuery() {
    return sqlQuery;
  }

  private void testConnection() {
    int selectInt = 1;
    String query = "select " + selectInt;
    try (Connection conn = this.dataSource.getConnection(); PreparedStatement pstmt = conn
      .prepareStatement(query); ResultSet rs = pstmt.executeQuery();) {
      if (rs.next()) {
        if (rs.getInt(1) == selectInt) {
          this.status = LogLineDescriptorStatus.INITED;
          LOGGER.info("DB inited.");
        } else {
          this.status = LogLineDescriptorStatus.ERROR;
          LOGGER.error("DB wrong.");
        }
      }
    } catch (SQLException e) {
      this.status = LogLineDescriptorStatus.ERROR;
      LOGGER.error("DB wrong.", e);
    }
  }

  private void setupDataSource() {
    String url = "jdbc:mysql://" + host + ":" + port + "/";
    this.dataSource = new BasicDataSource();
    this.dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    this.dataSource.setUsername(dbUserName);
    this.dataSource.setPassword(dbPassword);
    this.dataSource.setUrl(url);
  }

  private void initPositionFile() {
    this.positionRecorder = new PositionRecorder("d:/positions", getName() + ".position");
  }

  @Override
  public void close() throws IOException {
    try {
      this.dataSource.close();
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  private void buildQuerySql() throws LogLineSenderException {
    if (positionFieldDescriptor == null) {
      throw new LogLineSenderException("Cannot parse any row for line-position-recorder.");
    }

    StringBuilder sb = new StringBuilder("SELECT t.");
    sb.append(positionFieldDescriptor.getName());
    sb.append(SQL_FIELD_SEPARATOR);
    sb.append("t.");
    sb.append(uidFieldDescriptor.getName());
    sb.append(SQL_FIELD_SEPARATOR);

    boolean hasEvent = false;
    if (eventSize > 0) {
      hasEvent = true;
      Iterator<FieldDescriptor> it = eventItems.iterator();
      FieldDescriptor next;
      String rowName;
      for (; ; ) {
        next = it.next();
        rowName = next.getName();
        rowNames.add(rowName);
        sb.append("t.");
        sb.append(rowName);
        if (!it.hasNext()) {
          sb.append(' ');
          break;
        }
        sb.append(SQL_FIELD_SEPARATOR);
      }
    }

    boolean hasUP = false;
    if (upSize > 0) {
      hasUP = true;
      if (hasEvent) {
        sb.append(SQL_FIELD_SEPARATOR);
      }
      Iterator<FieldDescriptor> it = upItems.iterator();
      FieldDescriptor next;
      String rowName;
      for (; ; ) {
        next = it.next();
        rowName = next.getName();
        rowNames.add(rowName);
        sb.append("t.");
        sb.append(rowName);
        if (!it.hasNext()) {
          sb.append(' ');
          break;
        }
        sb.append(SQL_FIELD_SEPARATOR);
      }
    }

    if (CollectionUtils.isNotEmpty(valueOrTimestampItems)) {
      if (hasEvent || hasUP) {
        sb.append(SQL_FIELD_SEPARATOR);
      }
      Iterator<FieldDescriptor> it = valueOrTimestampItems.iterator();
      FieldDescriptor next;
      String rowName;
      for (; ; ) {
        next = it.next();
        rowName = next.getName();
        rowNames.add(rowName);
        sb.append("t.");
        sb.append(rowName);
        if (!it.hasNext()) {
          sb.append(' ');
          break;
        }
        sb.append(SQL_FIELD_SEPARATOR);
      }
    }

    sb.append("FROM ");
    sb.append(getName());
    sb.append(" AS t WHERE t.");
    sb.append(positionFieldDescriptor.getName());
    sb.append(" > ?");
    this.sqlQuery = sb.toString();
    LOGGER.info("Query SQL - " + this.sqlQuery);
  }

  private void fetchLinesFromDB() throws LogLineSenderException {
    long t1 = System.currentTimeMillis();
    long t2;
    int currentLineNum = -1, lastLineNum;
    try {
      lastLineNum = positionRecorder.read();
      LOGGER.info("Last operation line count - " + lastLineNum);
    } catch (IOException e) {
      throw new LogLineSenderException(e);
    }

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String positionColumnName = positionFieldDescriptor.getName();

    try {
      conn = dataSource.getConnection();
      pstmt = conn.prepareStatement(sqlQuery);
      pstmt.setInt(1, lastLineNum);
      rs = pstmt.executeQuery();

      HttpRequestEntityGroup entityGroup;
      Map<String, HttpRequestEntity> entityMap;
      HttpRequestEntity existEntity;
      String id, of, fieldName;
      long valOrTS;
      FieldType ft;
      while (rs.next()) {
        entityGroup = new HttpRequestEntityGroup(projectId, rs.getString(uidFieldDescriptor.getName()),
                                                 eventSize + upSize);
        entityMap = entityGroup.getEntityMap();
        currentLineNum = rs.getInt(positionColumnName);
        System.out.print(currentLineNum);
        System.out.print("\t");

        if (CollectionUtils.isNotEmpty(eventItems)) {
          for (FieldDescriptor fd : eventItems) {
            fieldName = rs.getString(fd.getName());
            if (StringUtils.isNoneBlank(fieldName)) {
              id = fd.getId();
              entityMap.put(id, new HttpRequestEntity(id, EVENT, fieldName));
            }
          }
        }
        if (CollectionUtils.isNotEmpty(upItems)) {
          for (FieldDescriptor fd : upItems) {
            fieldName = rs.getString(fd.getName());
            if (StringUtils.isNoneBlank(fieldName)) {
              id = fd.getId();
              entityMap.put(id, new HttpRequestEntity(id, UP, rs.getString(fd.getName())));
            }
          }
        }
        if (CollectionUtils.isNotEmpty(valueOrTimestampItems)) {
          for (FieldDescriptor fd : valueOrTimestampItems) {
            of = fd.getOf();
            existEntity = entityGroup.getEntityMap().get(of);
            if (existEntity == null) {
//              LOGGER.warn("This value or timestamp has no parent.");
              continue;
            }
            valOrTS = rs.getLong(fd.getName());
            if (valOrTS < 1) {
//              LOGGER.warn("Empty or 0 value will be ignored.");
              continue;
            }
            ft = fd.getType();
            if (TS.equals(ft)) {
              existEntity.setTimestamp(valOrTS);
            } else {
              existEntity.setFieldValue(valOrTS);
            }
          }
        }
        System.out.println(URLDecoder.decode(entityGroup.toURI().toString(), "utf8"));
      }
    } catch (Exception e) {
      throw new LogLineSenderException(e);
    } finally {
      if (currentLineNum >= 0) {
        lastLineNum = currentLineNum;
        try {
          positionRecorder.write(lastLineNum);
          LOGGER.info("This time operated log to line - " + currentLineNum);
        } catch (IOException e) {
          throw new LogLineSenderException(e);
        }
      } else {
        LOGGER.info("This time didn't operate any log. Last position is " + lastLineNum);
      }
      closeAutoCloseable(rs);
      closeAutoCloseable(pstmt);
      closeAutoCloseable(conn);
      t2 = System.currentTimeMillis();
      LOGGER.info("This round finished in " + (t2 - t1) + " milliseconds");
    }
  }

  @Override
  public void run() {
    TimeUnit timeUnit = sleepingParameter.getTimeUnit();
    long sleepingTime = sleepingParameter.getTime();
    try {
      while (enabled) {
        try {
          fetchLinesFromDB();
        } catch (LogLineSenderException e) {
          LOGGER.error("This round failed with exception.", e);
        }
        timeUnit.sleep(sleepingTime);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      signal.countDown();
    }
  }
}
