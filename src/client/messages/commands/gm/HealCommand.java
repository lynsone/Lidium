package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;

public class HealCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        c.getPlayer().getStat().heal(c.getPlayer());
        c.getPlayer().dispelDebuffs();
    }    
}