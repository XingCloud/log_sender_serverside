package com.xingcloud.sdk.serverside.test.mockdata;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * User: Z J Wu Date: 14-1-21 Time: 下午6:18 Package: com.xingcloud.sdk.serverside.test
 */
public class MockEventUPInsert {
  public static void main(String[] args) throws SQLException {
    String[] eventPool = new String[]{"gp.gain.shop", "gp.gain.system", "gp.consume.enhance.weapon.TwinBladesAzzinoth",
                                      "gp.consume.upgrade.chest.TalRashasGuardianship",
                                      "gp.consume.gamehall.portrait.SarahKerrigan", "gp.consume.equip.fashion.xmas",
                                      "gp.consume.equip.pet.black_dog", "gp.check", ""
    };
    String[] thPool = new String[]{"PeopleOfPi", "LiTianYi", "BoGuaGua", "CoalMineBoss"};
    Map<String, Integer> map = new HashMap<>();
    map.put("gp.consume.enhance.weapon.TwinBladesAzzinoth", 50);
    map.put("gp.consume.upgrade.chest.TalRashasGuardianship", 20);
    map.put("gp.consume.gamehall.portrait.SarahKerrigan", 150);
    map.put("gp.consume.equip.pet.black_dog", 200);
    map.put("gp.consume.equip.fashion.xmas", 100);

    String host = "localhost", user = "root", password = "1";
    int port = 3306;
    String url = "jdbc:mysql://" + host + ":" + port + "/";
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUsername(user);
    dataSource.setPassword(password);
    dataSource.setUrl(url);

    String sql1 = "insert into game_log.game_point (uid, event, event_val, timestamp) values (?, ?, ?, ?)";
    String sql2 = "insert into game_log.game_point (uid, event, event_val, property, property_val, timestamp) values (?, ?, ?, ?, ?, ?)";
    String sql3 = "insert into game_log.game_point (uid, event, timestamp) values (?, ?, ?)";
    String sql4 = "insert into game_log.game_point (uid, property, property_val) values (?, ?, ?)";

    Connection connection = dataSource.getConnection();
    PreparedStatement pstmt1 = connection.prepareStatement(sql1);
    PreparedStatement pstmt2 = connection.prepareStatement(sql2);
    PreparedStatement pstmt3 = connection.prepareStatement(sql3);
    PreparedStatement pstmt4 = connection.prepareStatement(sql4);
    Random random = new Random();
    String event, uid;
    DecimalFormat df = new DecimalFormat("000000");
    for (int i = 0; i < 100; i++) {
      event = eventPool[random.nextInt(eventPool.length)];
      uid = "USER_" + df.format(random.nextInt(100) + 1 + ((random.nextInt(5) + 1) * 100000));
      if (event.startsWith("gp.gain")) {
        pstmt1.setString(1, uid);
        pstmt1.setString(2, event);
        pstmt1.setString(3, String.valueOf((random.nextInt(20) + 1) * 10));
        pstmt1.setLong(4, new Date().getTime());
        pstmt1.execute();
      } else if (event.startsWith("gp.check")) {
        pstmt3.setString(1, uid);
        pstmt3.setString(2, event);
        pstmt3.setLong(3, new Date().getTime());
        pstmt3.execute();
      } else if (event.equals("")) {
        pstmt4.setString(1, uid);
        pstmt4.setString(2, "th_level");
        pstmt4.setString(3, thPool[random.nextInt(thPool.length)]);
        pstmt4.execute();
      } else {
        pstmt2.setString(1, uid);
        pstmt2.setString(2, event);
        pstmt2.setString(3, String.valueOf(map.get(event)));
        pstmt2.setLong(6, new Date().getTime());
        pstmt2.setString(4, "accumulated_gp_consumption");
        pstmt2.setString(5, String.valueOf(map.get(event)));
        pstmt2.execute();
      }
    }
    pstmt1.close();
    pstmt2.close();
    pstmt3.close();
    pstmt4.close();
    connection.close();
  }
}
