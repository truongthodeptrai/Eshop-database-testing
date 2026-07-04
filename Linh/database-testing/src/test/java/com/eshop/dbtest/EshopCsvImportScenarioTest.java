package com.eshop.dbtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EshopCsvImportScenarioTest {
  private static final Pattern TOKEN_PATTERN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

  private final HttpClient http = HttpClient.newHttpClient();
  private final String baseUrl = System.getProperty("eshop.baseUrl", "http://localhost:3000");
  private final String container =
      System.getProperty("eshop.backendContainer", "eshop-sut-backend-1");
  private String runId;

  @BeforeEach
  void requireExplicitE2eFlag() {
    Assumptions.assumeTrue(
        Boolean.getBoolean("eshop.e2e"),
        "Set -Deshop.e2e=true to run the localhost Docker EShop scenario");
    runId = "DBT-" + Instant.now().toEpochMilli();
  }

  @AfterEach
  void cleanupScenarioProducts() throws Exception {
    if (runId == null) {
      return;
    }
    String products = send("GET", "/api/products", null, null).body();
    Matcher matcher = Pattern.compile("\\{[^{}]*\"id\"\\s*:\\s*(\\d+)[^{}]*\"" + runId).matcher(products);
    while (matcher.find()) {
      send("DELETE", "/api/products/" + matcher.group(1), null, null);
    }
  }

  @Test
  void csvImportShouldRollbackWhenAnyRowIsInvalid() throws Exception {
    String adminToken = login("admin@eshop.com", "Admin123!");

    send(
        "POST",
        "/api/admin/import-products",
        adminToken,
        "{\"products\":["
            + productJson(runId + "-valid-before-invalid", 123456)
            + ",{\"name\":\"\",\"price\":777,\"description\":\"missing name\",\"imageUrl\":\"\",\"category_id\":1}"
            + "]}");

    assertEquals(
        0,
        countScenarioProducts(),
        "FR-16 failed: CSV import must be all-or-nothing. No products should be saved when one row is invalid.");
  }

  @Test
  void csvImportShouldRejectNegativePrice() throws Exception {
    String adminToken = login("admin@eshop.com", "Admin123!");

    send(
        "POST",
        "/api/admin/import-products",
        adminToken,
        "{\"products\":[" + productJson(runId + "-negative-price", -9000) + "]}");

    assertEquals(
        0,
        countScenarioProducts(),
        "FR-16 failed: product price must be positive. A negative-price product was saved.");
  }

  @Test
  void csvImportShouldRejectNonAdminUser() throws Exception {
    String userEmail = "dbt-user-" + System.currentTimeMillis() + "@example.test";
    register(userEmail);
    String userToken = login(userEmail, "User123!");

    HttpResponse<String> response =
        send(
            "POST",
            "/api/admin/import-products",
            userToken,
            "{\"products\":[" + productJson(runId + "-non-admin", 234567) + "]}");

    assertTrue(
        response.statusCode() == 401 || response.statusCode() == 403,
        "FR-12 failed: normal user should not access admin import API. Response was "
            + response.statusCode()
            + " "
            + response.body());
    assertEquals(
        0,
        countScenarioProducts(),
        "FR-12 failed: normal user should not be able to create products through admin import.");
  }

  @Test
  void tonicFabricateCsvShouldFollowSameRules() throws Exception {
    String adminToken = login("admin@eshop.com", "Admin123!");
    String userEmail = "tonic-java-" + System.currentTimeMillis() + "@example.test";
    register(userEmail);
    String userToken = login(userEmail, "User123!");
    List<ProductRow> rows = readTonicFabricateRows();

    send(
        "POST",
        "/api/admin/import-products",
        adminToken,
        "{\"products\":["
            + productJson(runId + "-" + rows.get(0).name(), rows.get(0).price())
            + ","
            + productJson("", rows.get(1).price())
            + ","
            + productJson(runId + "-" + rows.get(2).name(), rows.get(2).price())
            + "]}");

    HttpResponse<String> response =
        send(
            "POST",
            "/api/admin/import-products",
            userToken,
            "{\"products\":["
                + productJson(runId + "-" + rows.get(3).name(), rows.get(3).price())
                + "]}");

    assertTrue(
        response.statusCode() == 401 || response.statusCode() == 403,
        "FR-12 failed with Fabricate data: normal user should not access admin import API. Response was "
            + response.statusCode()
            + " "
            + response.body());
    assertEquals(
        0,
        countScenarioProducts(),
        "Fabricate AI data reproduced the bug: invalid and non-admin imports saved products.");
  }

  private void register(String email) throws Exception {
    HttpResponse<String> response =
        send(
            "POST",
            "/api/register",
            null,
            "{\"name\":\"DBT User\",\"email\":\"" + email + "\",\"password\":\"User123!\"}");
    assertEquals(200, response.statusCode(), response.body());
  }

  private String login(String email, String password) throws Exception {
    HttpResponse<String> response =
        send(
            "POST",
            "/api/login",
            null,
            "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");
    assertEquals(200, response.statusCode(), response.body());
    Matcher matcher = TOKEN_PATTERN.matcher(response.body());
    assertTrue(matcher.find(), response.body());
    return matcher.group(1);
  }

  private String productJson(String name, Number price) {
    return "{\"name\":\""
        + name
        + "\",\"price\":"
        + price
        + ",\"description\":\"database test product\",\"imageUrl\":\"\",\"category_id\":1}";
  }

  private HttpResponse<String> send(String method, String path, String token, String body)
      throws Exception {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder(URI.create(baseUrl + path)).header("Content-Type", "application/json");
    if (token != null) {
      builder.header("Authorization", "Bearer " + token);
    }
    if (body == null) {
      builder.method(method, HttpRequest.BodyPublishers.noBody());
    } else {
      builder.method(method, HttpRequest.BodyPublishers.ofString(body));
    }
    return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
  }

  private int countScenarioProducts() throws Exception {
    Path dbCopy = copyContainerDatabase();
    try (Connection jdbc = DriverManager.getConnection("jdbc:sqlite:" + dbCopy)) {
      IDatabaseConnection dbUnit = new DatabaseConnection(jdbc);
      ITable products =
          dbUnit.createQueryTable(
              "scenario_products",
              "SELECT id, name, price FROM products WHERE name LIKE '" + runId + "%'");
      return products.getRowCount();
    }
  }

  private List<ProductRow> readTonicFabricateRows() throws IOException {
    Path csv = Path.of("tonic-ai", "ai-variant-products.csv");
    List<String> lines = Files.readAllLines(csv);
    List<ProductRow> rows = new ArrayList<>();
    for (int index = 1; index < lines.size(); index++) {
      String[] columns = lines.get(index).split(",", -1);
      rows.add(new ProductRow(columns[0], Double.parseDouble(columns[1])));
    }
    return rows;
  }

  private Path copyContainerDatabase() throws IOException, InterruptedException {
    Path destination = Files.createTempFile("eshop-database-", ".sqlite");
    Process process =
        new ProcessBuilder("docker", "cp", container + ":/app/database.sqlite", destination.toString())
            .redirectErrorStream(true)
            .start();
    int exit = process.waitFor();
    if (exit != 0) {
      throw new IOException("docker cp failed with exit " + exit);
    }
    return destination;
  }

  private record ProductRow(String name, double price) {}
}
