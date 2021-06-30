/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.headgm;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.maps.MapleMap;

/**
 *
 * @author Manu
 */
public class WarpCommand extends Command {

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();
        if (params.length < 1) {
            player.dropMessage(-1, "Syntax: !warp <mapid>");
            return;
        }

        try {
            MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(params[0]));
            if (target == null) {
                player.dropMessage(-1, "Map ID " + params[0] + " is invalid.");
                return;
            }

            if (!player.isAlive()) {
                player.dropMessage(1, "You can't use this command if you're not alive.");
                return;
            }

            player.changeMap(target);
        } catch (Exception ex) {
            player.dropMessage(-1, "Map ID " + params[0] + " is invalid.");
        }
    }

}
