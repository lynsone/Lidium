package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;

public class OnlineChannelCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(6, "Characters connected to channel " + Integer.parseInt(splitted[1]) + ":");
        c.getPlayer().dropMessage(6, ChannelServer.getInstance(Integer.parseInt(splitted[1])).getPlayerStorage().getOnlinePlayers(true));
    }  
}