package app.config;

import java.sql.Connection;
import java.sql.Statement;

public final class Migrations {
    private Migrations() {}
    public static void run() throws Exception {
        try (Connection c = Db.getConnection(); Statement st = c.createStatement()) {
            st.execute("""
        CREATE TABLE IF NOT EXISTS users(
          id VARCHAR(64) PRIMARY KEY,
          name VARCHAR(120) NOT NULL,
          email VARCHAR(180) NOT NULL UNIQUE
        )""");

            st.execute("""
        CREATE TABLE IF NOT EXISTS items(
          id VARCHAR(64) PRIMARY KEY,
          name VARCHAR(160) NOT NULL,
          description VARCHAR(1000),
          price DECIMAL(12,2) NOT NULL,
          currency VARCHAR(8) NOT NULL
        )""");

            st.execute("""
        CREATE TABLE IF NOT EXISTS orders(
          id VARCHAR(64) PRIMARY KEY,
          user_id VARCHAR(64) NOT NULL,
          item_id VARCHAR(64) NOT NULL,
          quantity INT NOT NULL,
          total DECIMAL(12,2) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )""");
        }
    }
}
