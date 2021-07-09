/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands.admin;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import handling.world.World;
import tools.packet.CWvsContext;



public class TravelRateCommand extends Command {

    @Override
    public void execute(MapleClient c, String[] params) {
        if (params.length < 1) {
            c.getPlayer().dropMessage(6, "Syntax: !travelrate <newrate>");
            return;
        }

        int travelrate = Math.max(Integer.parseInt(params[0]), 1);
        c.getChannelServer().setTravelRate(travelrate);
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "[Rate] Travel Rate has been changed to " + travelrate + "x."));
    }
}
