package client.messages.commands.player;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import server.maps.MapleMap;
import server.maps.SavedLocationType;

public class JoinEventCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter victim = c.getPlayer();
        int event = victim.getClient().getChannelServer().getEvent();
        if (event != -1) {
            try {
                if (victim.getMapId() == event) {
                    victim.dropMessage(5, "You're already participating in this event.");
                    return;
                }

                victim.saveLocation(SavedLocationType.EVENT);
                MapleMap target = c.getChannelServer().getMapFactory().getMap(event);
                victim.changeMap(target, target.getPortal(0));
                return;
            } catch (Throwable $Throwable) {
                victim.dropMessage(5, "Something went wrong. Please contact a developer.");
                return;
            }

        }

        victim.dropMessage(5, "There currently is no event active to join.");
    }
}
