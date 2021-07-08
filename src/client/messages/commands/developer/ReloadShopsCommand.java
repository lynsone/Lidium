package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import server.MapleShopFactory;

public class ReloadShopsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleShopFactory.getInstance().clear();
        c.getPlayer().dropMessage(6, "Shops reloaded");
    } 
}