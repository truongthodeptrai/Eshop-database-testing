package com.eshop.dbtest;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.database.DatabaseConfig;
import org.junit.Before;
import org.junit.Test;

public class DbUnitHelloWorldTest {
  private IDatabaseConnection dbUnitConnection;

  @Before
  public void setUp() throws Exception {
    Connection jdbc =
        DriverManager.getConnection("jdbc:hsqldb:mem:dbunit_hello;DB_CLOSE_DELAY=-1", "sa", "");
    try (Statement statement = jdbc.createStatement()) {
      statement.execute(
          "CREATE TABLE IF NOT EXISTS USER_ACCOUNT ("
              + "ID INTEGER PRIMARY KEY, "
              + "NAME VARCHAR(100), "
              + "EMAIL VARCHAR(120))");
    }

    dbUnitConnection = new DatabaseConnection(jdbc);
    dbUnitConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
    IDataSet dataSet =
        new FlatXmlDataSetBuilder()
        .build(getClass().getResourceAsStream("/dbunit/hello-users.xml"));
    DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
  }

  @Test
  public void testHelloWorldDatasetIsLoaded() throws Exception {
    assertEquals(2, dbUnitConnection.getRowCount("USER_ACCOUNT"));
  }
}
