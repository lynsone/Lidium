package client.messages.commands.gm;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.messages.Command;

public class KillCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleCharacter player = c.getPlayer();
        if(splitted.length < 1){
            c.getPlayer().dropMessage(6, "Syntax: !kill <list player names>");
            return;
        }
        MapleCharacter victim = null;
        for(int i = 1; i < splitted.length; i++){
            try {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[i]);
            }catch(Exception e){
                c.getPlayer().dropMessage(6, "Player " + splitted[i] + " not found.");
            }
            if(player.allowedToTarget(victim) && player.getGMLevel() >= victim.getGMLevel()){
                victim.getStat().setHp((short) 0, victim);
                victim.getStat().setMp((short) 0, victim);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
        }
    }    
}