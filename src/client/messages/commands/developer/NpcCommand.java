package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import tools.packet.CField.NPCPacket;

public class NpcCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        int npcId = Integer.parseInt(splitted[0]);
        MapleNPC npc = MapleLifeFactory.getNPC(npcId);
        if (npc != null && !npc.getName().equals("MISSINGNO")) {
            npc.setPosition(c.getPlayer().getPosition());
            npc.setCy(c.getPlayer().getPosition().y);
            npc.setRx0(c.getPlayer().getPosition().x + 50);
            npc.setRx1(c.getPlayer().getPosition().x - 50);
            npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            npc.setCustom(true);
            c.getPlayer().getMap().addMapObject(npc);
            c.getPlayer().getMap().broadcastMessage(NPCPacket.spawnNPC(npc, true));
        } else {
            c.getPlayer().dropMessage(6, "You have entered an invalid Npc-Id");
        }
    }  
}