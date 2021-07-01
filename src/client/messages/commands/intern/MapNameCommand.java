package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;

public class MapNameCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(5, "You are on map " + c.getPlayer().getMap().getId());
    }    
}