package id.shoestore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
  private Database() {}

  public static Connection open() throws SQLException {
    try {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    } catch (ClassNotFoundException ex) {
      throw new SQLException("JDBC driver unavailable", ex);
    }
    String url =
        "jdbc:sqlserver://"
            + setting("DB_HOST", "127.0.0.1")
            + ":"
            + setting("DB_PORT", "1433")
            + ";databaseName="
            + setting("DB_NAME", "shoestore")
            + ";encrypt=true;trustServerCertificate="
            + "true".equalsIgnoreCase(System.getenv("DB_TRUST_CERTIFICATE"))
            + ";loginTimeout=5;socketTimeout=5000";
    return DriverManager.getConnection(
        url, setting("DB_USER", "shoestore_app"), required("DB_PASSWORD"));
  }

  public static String required(String key) {
    String value = System.getenv(key);
    if (value == null || value.isBlank())
      throw new IllegalStateException("Missing environment variable: " + key);
    return value;
  }

  private static String setting(String key, String fallback) {
    String value = System.getenv(key);
    return value == null || value.isBlank() ? fallback : value;
  }
}
