/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.player;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import database.DatabaseConnection;
import java.util.Iterator;
import server.MapleItemInformationProvider;
import java.sql.*;
import server.ItemInformation;
import server.life.MapleMonsterInformationProvider;

/**
 *
 * @author Manu
 */
public class WhoDropsCommand extends Command {

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.dropMessage(5, "Synax: @whodrops <item name>");
            return;
        }

        String searchString = player.getLastCommandMessage();
        String output = "Restults for " + searchString + "\r\n";
        Iterator<ItemInformation> listIterator = MapleItemInformationProvider.getInstance().getItemDataByName(searchString).iterator();
        if (listIterator.hasNext()) {
           
            while (listIterator.hasNext() /*&& count <= 3*/) {
                ItemInformation data = listIterator.next();

                output += "#b" + data.name + "#k is dropped by:\r\n\r\n";
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data WHERE itemid = ?;");
                    ps.setInt(1, data.itemId);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String resultName = MapleMonsterInformationProvider.getInstance().getMobNameFromId(rs.getInt("dropperid")).trim();
                        if (resultName != null || resultName != "" || resultName.length() != 0) {
                            output += "-" + resultName + " (ID: " + rs.getInt("dropperid") + ")\r\n";
                        }
                    }
                    rs.close();
                    ps.close();
                    con.close();
                } catch (Exception e) {
                    player.dropMessage(6, "There was a problem retrieving the required data. Please try again.");
                    e.printStackTrace();
                    return;
                }
                output += "\r\n\r\n";
               
            }
        } else {
            player.dropMessage(5, "The item you searched for doesn't exist.");
            return;
        }

        c.getAbstractPlayerInteraction().npcTalk(9010000, output);

    }

}
