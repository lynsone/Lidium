package client.messages.commands.headgm;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.messages.Command;

public class FameCommand extends Command{

    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleCharacter player = c.getPlayer();
        
        if (splitted.length < 2){
            c.getPlayer().dropMessage(6, "Syntax: !fame <player> <amount>");
        }
        
        MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        int fame = 0;
        try{
            fame = Integer.parseInt(splitted[1]);
        } catch (NumberFormatException nfe) {
            c.getPlayer().dropMessage(6, "Invalid Number...");
        }
        if (victim != null && player.allowedToTarget(victim)) {
            victim.addFame(fame);
            victim.updateSingleStat(MapleStat.FAME, victim.getFame());
        }
    }
}