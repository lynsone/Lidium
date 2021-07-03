package server;

import constants.GameConstants;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import database.DatabaseConnection;

/**
 *
 * @author Emilyx3
 */
public class ServerProperties {

    private static final Properties props = new Properties();

    private ServerProperties() {
    }

    static {
        loadProperties("db.properties");
        loadProperties("channel.properties");
        if (getProperty("GMS") != null) {
            GameConstants.GMS = Boolean.parseBoolean(getProperty("GMS"));
        }
        var con=DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM auth_server_channel_ip"); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0); //Big ass error.
        }finally{
            try {
                if(con!=null && !con.isClosed()){
                    con.close();
                }
            } catch (Exception ignore) {
            }
        }
        loadProperties(GameConstants.GMS ? "worldGMS.properties" : "world.properties");

    }

    public static void loadProperties(String s) {
        FileReader fr;
        try {
            fr = new FileReader(s);
            props.load(fr);
            fr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static String getProperty(String s, String def) {
        return props.getProperty(s, def);
    }
}
