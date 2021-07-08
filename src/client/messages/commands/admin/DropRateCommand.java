package client.messages.commands.admin;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.packet.CWvsContext;

public class DropRateCommand extends Command {

    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length <1){
            c.getPlayer().dropMessage(6, "Syntax: !droprate <new rate>");
            return;
        }
        
        final int DropRate = Integer.parseInt(splitted[0]);
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(DropRate);
                        
        }
        World.Broadcast.broadcastMessage(CWvsContext.serverNotice(6, "Exprate has been changed to " + DropRate + "x."));
    }  
}