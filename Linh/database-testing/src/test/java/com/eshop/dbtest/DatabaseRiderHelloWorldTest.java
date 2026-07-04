package com.eshop.dbtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@DBRider
@DBUnit(caseSensitiveTableNames = true, cacheConnection = false)
class DatabaseRiderHelloWorldTest {
  private static Connection connection;

  @SuppressWarnings("unused")
  private final ConnectionHolder connectionHolder = () -> connection;

  @BeforeAll
  static void createSchema() throws Exception {
    connection =
        DriverManager.getConnection("jdbc:hsqldb:mem:rider_hello;DB_CLOSE_DELAY=-1", "sa", "");
    try (Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS PRODUCT");
      statement.execute(
          "CREATE TABLE PRODUCT ("
              + "ID INTEGER PRIMARY KEY, "
              + "NAME VARCHAR(100), "
              + "PRICE INTEGER)");
    }
  }

  @AfterAll
  static void closeConnection() throws Exception {
    if (connection != null) {
      connection.close();
    }
  }

  @org.junit.jupiter.api.Test
  @DataSet(value = "rider/products.yml", cleanBefore = true)
  @ExpectedDataSet("rider/expected-products.yml")
  void helloWorldDatasetIsSeededAndAsserted() throws Exception {
    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM PRODUCT")) {
      resultSet.next();
      assertEquals(2, resultSet.getInt(1));
    }
  }
}
