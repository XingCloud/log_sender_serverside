package com.xingcloud.sdk.serverside.model;

import static com.xingcloud.sdk.serverside.LogSenderServerSideUtils.closeAutoCloseable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xingcloud.sdk.serverside.LogLineSenderException;
import com.xingcloud.sdk.serverside.enums.LogLineDescriptorStatus;
import com.xingcloud.sdk.serverside.enums.LogType;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Z J Wu Date: 14-1-21 Time: 上午10:14 Package: com.xingcloud.sdk.serverside.model
 */
public class DBLogLineDescriptor extends LogLineDescriptor implements Closeable, AutoCloseable, Runnable {
  private static final Logger LOGGER = Logger.getLogger(DBLogLineDescriptor.class);
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

  public DBLogLineDescriptor() {
  }

  public DBLogLineDescriptor(String name, LogType type, List<LogLineFieldDescriptor> items, String host, int port,
                             String dbUserName, String dbPassword) throws LogLineSenderException {
    super(name, type, items);
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public LogType getType() {
    return type;
  }

  public void setType(LogType type) {
    this.type = type;
  }

  public List<LogLineFieldDescriptor> getItems() {
    return items;
  }

  public void setItems(List<LogLineFieldDescriptor> items) {
    this.items = items;
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
    StringBuilder sb = new StringBuilder("SELECT ");
    Iterator<LogLineFieldDescriptor> it = items.iterator();
    LogLineFieldDescriptor next;
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
      sb.append(", ");
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
    int currentLineNum = 0, lastLineNum = 0;
    try {
      lastLineNum = positionRecorder.read();
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

      while (rs.next()) {

      }
    } catch (SQLException e) {
      throw new LogLineSenderException(e);
    } finally {
      closeAutoCloseable(rs);
      closeAutoCloseable(pstmt);
      closeAutoCloseable(conn);
    }
  }

  @Override public void run() {

  }
}
