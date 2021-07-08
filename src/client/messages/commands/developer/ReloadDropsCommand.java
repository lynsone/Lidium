package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import scripting.ReactorScriptManager;
import server.life.MapleMonsterInformationProvider;

public class ReloadDropsCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleMonsterInformationProvider.getInstance().clearDrops();
        ReactorScriptManager.getInstance().clearDrops();
        c.getPlayer().dropMessage(6, "Drops reloaded");
    }   
}