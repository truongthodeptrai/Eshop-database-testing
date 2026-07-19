package org.database.testing.framework;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class DatabaseTestConfig {
  private final Properties fileProperties = new Properties();

  private DatabaseTestConfig() {
    String configFile = System.getProperty("database.test.config", "");
    if (configFile.isBlank()) {
      return;
    }
    try (InputStream input = openConfig(configFile)) {
      if (input != null) {
        fileProperties.load(input);
      } else {
        throw new IllegalStateException("Missing database test config: " + configFile);
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Cannot load database test config: " + configFile, exception);
    }
  }

  public static DatabaseTestConfig load() {
    return new DatabaseTestConfig();
  }

  public String value(String key) {
    String systemValue = System.getProperty(key);
    if (systemValue != null && !systemValue.isBlank()) {
      return systemValue;
    }

    String envValue = System.getenv(toEnvName(key));
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    return fileProperties.getProperty(key);
  }

  public String value(String key, String defaultValue) {
    String value = value(key);
    return value == null || value.isBlank() ? defaultValue : value;
  }

  public int intValue(String key, int defaultValue) {
    return Integer.parseInt(value(key, Integer.toString(defaultValue)));
  }

  public boolean booleanValue(String key, boolean defaultValue) {
    return Boolean.parseBoolean(value(key, Boolean.toString(defaultValue)));
  }

  public Path pathValue(String key) {
    return Path.of(value(key)).toAbsolutePath().normalize();
  }

  public List<String> listValue(String key) {
    String value = value(key, "");
    if (value.isBlank()) {
      return List.of();
    }
    return Arrays.stream(value.split(","))
        .map(String::trim)
        .filter(item -> !item.isBlank())
        .toList();
  }

  private String toEnvName(String key) {
    return key.toUpperCase().replace('.', '_').replace('-', '_');
  }

  private InputStream openConfig(String configFile) throws IOException {
    InputStream classpathInput = DatabaseTestConfig.class.getResourceAsStream(configFile);
    if (classpathInput != null) {
      return classpathInput;
    }

    Path path = Path.of(configFile);
    if (Files.exists(path)) {
      return Files.newInputStream(path);
    }

    return null;
  }
}
