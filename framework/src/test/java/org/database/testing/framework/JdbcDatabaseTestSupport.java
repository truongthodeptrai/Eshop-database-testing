package org.database.testing.framework;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.configuration.DBUnitConfig;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.dataset.DataSetExecutorImpl;
import org.dbunit.database.DatabaseConfig;

public final class JdbcDatabaseTestSupport {
  private final DatabaseTestConfig config;

  public JdbcDatabaseTestSupport(DatabaseTestConfig config) {
    this.config = config;
  }

  public Connection openConnection() throws SQLException {
    String driver = config.value("db.driver", "");
    if (!driver.isBlank()) {
      try {
        Class.forName(driver);
      } catch (ClassNotFoundException exception) {
        throw new IllegalStateException("Cannot load JDBC driver: " + driver, exception);
      }
    }
    return DriverManager.getConnection(
        config.value("db.url"),
        config.value("db.user", ""),
        config.value("db.password", ""));
  }

  public void applySchema(Connection connection) throws Exception {
    String schemaResource = config.value("db.schema", "");
    if (schemaResource.isBlank()) {
      return;
    }
    String schema = readResource(schemaResource);
    try (Statement statement = connection.createStatement()) {
      for (String sql : schema.split(";")) {
        if (!sql.isBlank()) {
          statement.execute(sql.trim());
        }
      }
    }
  }

  public void seedWithDatabaseRider(Connection connection) throws Exception {
    String seedResource = config.value("db.seed", "");
    if (seedResource.isBlank()) {
      return;
    }
    DataSetConfig dataSet =
        new DataSetConfig(resolveDataSetName(seedResource))
            .strategy(SeedStrategy.valueOf(config.value("db.seed.strategy", "CLEAN_INSERT")))
            .cleanBefore(config.booleanValue("db.seed.cleanBefore", false))
            .disableConstraints(config.booleanValue("db.rider.disableConstraints", true));
    rider(connection).createDataSet(dataSet);
  }

  public void assertWithDatabaseRider(Connection connection) throws Exception {
    String expectedResource = config.value("db.assert.dataset", "");
    if (expectedResource.isBlank()) {
      return;
    }
    DataSetConfig expected = new DataSetConfig(resolveDataSetName(expectedResource));
    CompareOperation operation =
        CompareOperation.valueOf(config.value("db.assert.compare", "EQUALS"));
    rider(connection)
        .compareCurrentDataSetWith(
            expected,
            config.listValue("db.assert.ignoreColumns").toArray(String[]::new),
            null,
            config.listValue("db.assert.orderBy").toArray(String[]::new),
            operation);
  }

  public void executeSql(Connection connection, String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  public void executeConfiguredSql(Connection connection, String keyPrefix) throws SQLException {
    for (String id : config.listValue(keyPrefix + ".ids")) {
      executeSql(connection, config.value(keyPrefix + "." + id));
    }
  }

  private String readResource(String resource) throws IOException {
    try (InputStream input = openResource(resource)) {
      if (input == null) {
        throw new IllegalArgumentException("Missing test resource: " + resource);
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private InputStream openResource(String resource) throws IOException {
    InputStream classpathInput = getClass().getResourceAsStream(resource);
    if (classpathInput != null) {
      return classpathInput;
    }
    return null;
  }

  private String resolveDataSetName(String resource) throws IOException {
    Path path = Path.of(resource);
    if (Files.exists(path)) {
      return path.toAbsolutePath().normalize().toUri().toURL().toString();
    }
    return resource;
  }

  private DataSetExecutorImpl rider(Connection connection) throws Exception {
    ConnectionHolder holder = () -> connection;
    DBUnitConfig dbUnitConfig =
        new DBUnitConfig()
            .columnSensing(true)
            .cacheConnection(false)
            .addDBUnitProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
    return DataSetExecutorImpl.instance(
        "framework-" + System.identityHashCode(connection), holder, dbUnitConfig);
  }
}
