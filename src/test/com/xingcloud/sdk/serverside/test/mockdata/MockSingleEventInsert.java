package com.xingcloud.sdk.serverside.test.mockdata;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午6:18 Package: com.xingcloud.sdk.serverside.test
 */
public class MockSingleEventInsert {
  public static void main(String[] args) throws SQLException, InterruptedException {
    String[] eventPool = new String[]{"coin.gain.buy", "coin.gain.from_system",
                                      "coin.consume.enhance.weapon.TwinBladesAzzinoth",
                                      "coin.consume.enhance.weapon.Shadowmourne",
                                      "coin.consume.enhance.equipment.helm.t6"

    };

    while (true) {
      String host = "localhost", user = "root", password = "1";
      int port = 3306;
      String url = "jdbc:mysql://" + host + ":" + port + "/";
      BasicDataSource dataSource = new BasicDataSource();
      dataSource.setDriverClassName("com.mysql.jdbc.Driver");
      dataSource.setUsername(user);
      dataSource.setPassword(password);
      dataSource.setUrl(url);

      String sql = "insert into game_log.coin (uid, event, number, timestamp) values (?, ?, ?, ?)";
      Connection connection = dataSource.getConnection();
      PreparedStatement pstmt = connection.prepareStatement(sql);
      Random random = new Random();
      int j = 100 + random.nextInt(300);
      System.out.println(j);
      for (int i = 0; i < 100; i++) {
        pstmt.setString(1, RandomStringUtils.randomAlphanumeric(10));
        pstmt.setString(2, eventPool[random.nextInt(eventPool.length)]);
        pstmt.setInt(3, random.nextInt(100));
        pstmt.setLong(4, new Date().getTime());
        pstmt.execute();
      }

      pstmt.close();
      connection.close();

      TimeUnit.SECONDS.sleep(20);
    }

  }
}
