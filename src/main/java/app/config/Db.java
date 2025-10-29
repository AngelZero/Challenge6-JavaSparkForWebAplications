package app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:h2:mem:collectibles;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", "");
    }
}
