package client.messages.commands.admin;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.messages.Command;
import server.life.PlayerNPC;
import tools.MockIOSession;


public class OfflinePlayerNpcCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length <2){
            c.getPlayer().dropMessage(6, "Syntax: !offlineplayernpc <Character Name> <Script id>");
            return;
        }
        try {
            c.getPlayer().dropMessage(6, "Making playerNPC...");
            MapleClient bitch = new MapleClient(null, null, new MockIOSession());
            MapleCharacter chr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted[0]), bitch, false);
            if (chr == null) {
                c.getPlayer().dropMessage(6, splitted[0] + " does not exist");
                return;
            }
            PlayerNPC npc = new PlayerNPC(chr, Integer.parseInt(splitted[1]), c.getPlayer().getMap(), c.getPlayer());
            npc.addToServer();
            c.getPlayer().dropMessage(6, "Done");
        } catch (NumberFormatException e) {
            c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
        }
    }
}