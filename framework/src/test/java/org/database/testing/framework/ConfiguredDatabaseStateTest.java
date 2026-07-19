package org.database.testing.framework;

import java.sql.Connection;
import org.junit.jupiter.api.Test;

class ConfiguredDatabaseStateTest {
  @Test
  void configuredDatabaseStatePasses() throws Exception {
    DatabaseTestConfig config = DatabaseTestConfig.load();
    JdbcDatabaseTestSupport database = new JdbcDatabaseTestSupport(config);

    try (Connection connection = database.openConnection()) {
      database.applySchema(connection);
      database.executeConfiguredSql(connection, "db.cleanup.sql");
      database.seedWithDatabaseRider(connection);
      database.executeConfiguredSql(connection, "db.seed.sql");
      database.assertWithDatabaseRider(connection);
    }
  }
}
