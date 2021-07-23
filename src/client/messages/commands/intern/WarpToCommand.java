package client.messages.commands.intern;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.Command;
import handling.channel.ChannelServer;
import handling.world.World;

public class WarpToCommand extends Command {
    @Override
    public void execute(MapleClient c, String[] splitted) {
        if (splitted.length <= 0) {
            c.getPlayer().dropMessage(5, "!warpto <playername>");
            return;
        }

        MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[0]);
        if (target != null) {
            c.getPlayer().changeMap(target.getMap(), target.getMap().findClosestPortal(target.getTruePosition()));
        } else {
            int ch = World.Find.findChannel(splitted[0]);
            if (ch < 0) {
                c.getPlayer().dropMessage(5, "Target player not found.");
                return;
            }

            target = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[0]);
            c.getPlayer().dropMessage(5, "Cross changing channel.");
        
            if (target.getMapId() != c.getPlayer().getMapId()) {
                c.getPlayer().changeMap(target.getMap(), target.getMap().findClosestPortal(target.getTruePosition()));
            }
            c.getPlayer().changeChannel(target.getClient().getChannel());
        }
    }
}
