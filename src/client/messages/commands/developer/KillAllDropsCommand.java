package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import java.util.Arrays;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class KillAllDropsCommand extends Command {
    
    @Override
    public void execute(MapleClient c, String[] splitted){
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;                     
        MapleMonster mob;
        for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            mob = (MapleMonster) monstermo;
            map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
        }
    }   
}