package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import constants.GameConstants;
import handling.channel.ChannelServer;
import server.maps.MapleMap;
import server.quest.MapleQuest;

public class JailCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length < 2) {
            c.getPlayer().dropMessage(6, "jail [name] [minutes, 0 = forever]");
            return;
        }
        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        final int minutes = Math.max(0, Integer.parseInt(splitted[1]));
        if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
            MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(GameConstants.JAIL);
            victim.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData(String.valueOf(minutes * 60));
            victim.changeMap(target, target.getPortal(0));
        } else {
            c.getPlayer().dropMessage(6, "Please be on their channel.");
        }
    }   
}