package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;

public class SpawnDebugCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
    }    
}
