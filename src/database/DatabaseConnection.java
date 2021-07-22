package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import server.ServerProperties;

/**
 * @author Manu -
 */
public class DatabaseConnection {

    private static final DatabaseConnection dbc = new DatabaseConnection();

    public static DatabaseConnection getInstance() {
        return dbc;
    }

    public static Connection getConnection() {
        return getInstance().getInternalConnection();
    }

    private Connection getInternalConnection() {
        int denies = 0;
        while (true) { // There is no way it can pass with a null out of here?
            try {

                return DriverManager.getConnection(
                        ServerProperties.getProperty("database.url",
                                "jdbc:mysql://localhost:3306/v111?autoReconnect=true"),
                        ServerProperties.getProperty("database.user", "root"),
                        ServerProperties.getProperty("database.password", "root"));
            } catch (SQLException sqle) {
                denies++;

                if (denies == 3) {
                    System.err.println(sqle);
                    break;
                }
            }
        }
        return null;
    }

    public DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // touch the mysql driver
        } catch (ClassNotFoundException e) {
            System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
            System.err.println(e);
        }

    }
}
