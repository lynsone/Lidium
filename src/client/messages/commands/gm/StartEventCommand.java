package client.messages.commands.gm;

import client.MapleClient;
import client.messages.Command;
import server.events.MapleEvent;

public class StartEventCommand extends Command {
    @Override
        public void execute(MapleClient c, String[] splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "Started the event and closed off");
            } else {
                c.getPlayer().dropMessage(5, "!scheduleevent must've been done first, and you must be in the event map.");
            }
        }
}
