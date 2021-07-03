package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import server.ServerProperties;

public class DatabaseConnection {
    private static HikariDataSource ds;

    public static Connection getConnection() {
        if (ds != null) {
            try {
                return ds.getConnection();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        for (int denies = 0; denies <= 3; denies++) { // There is no way it can pass with a null out of here?
            try {
                return DriverManager.getConnection(
                        ServerProperties.getProperty("database.url",
                                "jdbc:mysql://localhost:3306/v111?autoReconnect=true"),
                        ServerProperties.getProperty("database.user", "root"),
                        ServerProperties.getProperty("database.password", "root"));
            } catch (SQLException sqle) {
                denies++;

                if (denies == 3) {
                    sqle.printStackTrace();
                }
            }
        }
        return null;
    }

    private static int getNumberOfAccounts() {
        try {
            Connection con = DriverManager.getConnection(
                    ServerProperties.getProperty("database.url", "jdbc:mysql://localhost:3306/v111?autoReconnect=true"),
                    ServerProperties.getProperty("database.user", "root"),
                    ServerProperties.getProperty("database.password", "root"));
            try (PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM accounts")) {
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            } finally {
                con.close();
            }
        } catch (SQLException sqle) {
            return 20;
        }
    }

    public DatabaseConnection() {
        try {
            Class.forName(ServerProperties.getProperty("database.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (ClassNotFoundException e) {
            System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
            e.printStackTrace();
        }

        ds = null;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                ServerProperties.getProperty("database.url", "jdbc:mysql://localhost:3306/v111?autoReconnect=true"));

        config.setUsername(ServerProperties.getProperty("database.user", "root"));
        config.setPassword(ServerProperties.getProperty("database.password", "root"));

        // Make sure pool size is comfortable for the worst case scenario.
        // Under 100 accounts? Make it 10. Over 10000 accounts? Make it 30.
        int poolSize = (int) Math.ceil(0.00202020202 * getNumberOfAccounts() + 9.797979798);
        if (poolSize < 10) {
            poolSize = 10;
        } else if (poolSize > 30) {
            poolSize = 30;
        }

        config.setConnectionTimeout(30 * 1000);
        config.setMaximumPoolSize(poolSize);

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 25);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.setLeakDetectionThreshold(20000);
        config.setMaxLifetime(60000 * 5);
        config.setMinimumIdle(5);
        ds = new HikariDataSource(config);

    }

    public static void closeAll() {
        ds.close();
    }
}
