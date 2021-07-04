package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import server.ServerProperties;

/*
Author: Manu
*/
public class DatabaseConnection {

    private static Connection con = null;

    public static Connection getConnection() {

        try {

            for (int i = 0; i <= 3; i++) {
                con = DriverManager.getConnection(
                        ServerProperties.getProperty("database.url",
                                "jdbc:mysql://localhost:3306/v111?autoReconnect=true"),
                        ServerProperties.getProperty("database.user", "root"),
                        ServerProperties.getProperty("database.password", "root"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public static void closeAll() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
