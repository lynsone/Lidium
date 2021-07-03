package client.messages.commands.intern;

import client.MapleClient;
import client.messages.Command;
import java.util.Arrays;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

public class MonsterDebugCommand extends Command {

    @Override
    public void execute(MapleClient c, String[] splitted) {
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;

        if (splitted.length > 0) {
            int irange = Integer.parseInt(splitted[0]);
            if (splitted.length <= 2) {
                range = irange * irange;
            } else {
                map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
            }
        }
        if (map == null) {
            c.getPlayer().dropMessage(6, "Map does not exist");
            return;
        }
        MapleMonster mob;
        for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
            mob = (MapleMonster) monstermo;
            c.getPlayer().dropMessage(6, "Monster " + mob.toString());
        }
    }
}
