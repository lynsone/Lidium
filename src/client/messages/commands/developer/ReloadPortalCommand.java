package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import scripting.PortalScriptManager;

public class ReloadPortalCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted){
        PortalScriptManager.getInstance().clearScripts();
        c.getPlayer().dropMessage(6, "Portal scripts reloaded");
    }  
}