package client.messages.commands.admin;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.life.PlayerNPC;

public class PlayerNpcCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        if (splitted.length <2){
            c.getPlayer().dropMessage(6, "Syntax: !playernpc <Character Name> <Script id>");
            return;
        }
        try{
            c.getPlayer().dropMessage(6, "Making Player NPC");
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
            if(chr == null){
                c.getPlayer().dropMessage(6, splitted[0] + " is not online");
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