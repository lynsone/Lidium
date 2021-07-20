package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import server.events.MapleEvent;
import server.events.MapleEventType;

public class ScheduleEventCommand extends Command {
    @Override
        public void execute(MapleClient c, String[] splitted) {
            MapleEventType type;
            try {
                type = MapleEventType.getByString(splitted[0]);
            } catch (ArrayIndexOutOfBoundsException $ArrayIndexOutOfBoundsException) {
                type = null;
            }

            if (type == null) {
                final StringBuilder sb = new StringBuilder("Wrong syntax: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
                return;
            }

            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
                return;
            }
        }
}
