package client.messages.commands.developer;

import client.MapleClient;
import client.messages.Command;
import server.maps.MapleMapObject;
import server.maps.MapleReactor;

public class LookReactorCommand extends Command{
    
    @Override
    public void execute(MapleClient c, String[] splitted) {
        for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
            MapleReactor reactor2l = (MapleReactor) reactor1l;
            c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
        }
    }
}