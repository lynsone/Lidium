package client.messages.commands.player;

import client.MapleClient;
import client.messages.Command;
import java.util.Arrays;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class MobCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] params){
        MapleMonster mob = null;
        for(final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))){            
            mob = (MapleMonster) monstermo;
            if(mob.isAlive()) {
                c.getPlayer().dropMessage(6, "Monster " + mob.toString());
                return;
            }
        }
        
        if (mob == null) {
            c.getPlayer().dropMessage(6, "No monster was found.");   
        }
    }  
}