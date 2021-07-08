package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;

public class ReloadEventsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        for (ChannelServer instance : ChannelServer.getAllInstances()) {
            instance.reloadEvents();
        }
        c.getPlayer().dropMessage(6, "Events reloaded");
    }    
}