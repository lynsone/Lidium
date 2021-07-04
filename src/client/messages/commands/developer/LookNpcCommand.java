package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import server.life.MapleNPC;
import server.maps.MapleMapObject;

public class LookNpcCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
            MapleNPC reactor2l = (MapleNPC) reactor1l;
            c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
        }
    }
}