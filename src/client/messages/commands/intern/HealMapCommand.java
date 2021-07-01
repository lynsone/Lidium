package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.messages.Command;

public class HealMapCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleCharacter player = c.getPlayer();
        for (MapleCharacter chr : player.getMap().getCharacters()) {
            if (chr != null){
                chr.getStat().setHp(chr.getStat().getMaxHp(), chr);
                chr.updateSingleStat(MapleStat.HP, chr.getStat().getMaxHp());
                chr.getStat().setMp(chr.getStat().getMaxMp(), chr);
                chr.updateSingleStat(MapleStat.MP, chr.getStat().getMaxMp());
                chr.dispelDebuffs();
            }
        }
    }   
}