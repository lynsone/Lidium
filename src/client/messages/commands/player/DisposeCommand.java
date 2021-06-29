package client.messages.commands.player;

import client.messages.Command;
import client.MapleClient;
import scripting.NPCScriptManager;
import tools.packet.CWvsContext;

public class DisposeCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] params){       
        c.removeClickedNPC();
        NPCScriptManager.getInstance().dispose(c);
        c.getSession().write(CWvsContext.enableActions());
        c.getPlayer().blueMessage("You've been disposed.");
    }
}