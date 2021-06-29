package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import scripting.NPCScriptManager;

public class CheckDropsCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (c.getPlayer().hasBlockedInventory()  || c.getPlayer().isInBlockedMap()){
            c.getPlayer().dropMessage(5, "You may not use this command here.");
        }else{ 
            NPCScriptManager.getInstance().start(c, 9010000);
        }
    }
}