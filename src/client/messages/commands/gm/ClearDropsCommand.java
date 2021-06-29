package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;

public class ClearDropsCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(5, "Cleared " + c.getPlayer().getMap().getNumItems() + " drops");
        c.getPlayer().getMap().removeDrops();
    }  
}